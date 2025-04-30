package com.whdcks3.portfolio.gory_server.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.whdcks3.portfolio.gory_server.data.models.squad.Squad;
import com.whdcks3.portfolio.gory_server.firebase.FirebasePublisherUtil;
import com.whdcks3.portfolio.gory_server.firebase.representation.MulticastMessageRepresentation;
import com.whdcks3.portfolio.gory_server.repositories.SquadRepository;
import com.whdcks3.portfolio.gory_server.service.interfaces.ISquadFcm;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FirebaseMessagingService implements ISquadFcm {
    private final FirebasePublisherUtil firebasePublisherUtil;
    private final SquadRepository squadRepository;

    @Override
    public void squadNewMemberJoined(Squad squad) {
        String fcmToken = squad.getUser().getFcmToken();
        boolean isAlarm = squad.getUser().getFeedAlarm();
        String title = "모임에 새로운 멤버가 참여했습니다.";
        String content = String.format("회원님의 모임글 [%s]에 새로운 멤버가 참여했습니다.",
                squad.getTitle().substring(0, Math.max(20, squad.getTitle().length())));
        String data = squad.getPid() + ",squad";

        sendPushToClient(isAlarm, fcmToken, title, content, data);
    }

    @Override
    @Scheduled(cron = "0 0,30 * * * *")
    public void squad1DNotifying() {
        List<Squad> squads = squadRepository.findAll();

        LocalDateTime now = LocalDateTime.now();

        for (Squad squad : squads) {
            LocalDateTime meetingDateTime = squad.resolveMeetingDateTime();
            LocalDateTime notifiyTime = meetingDateTime.minusHours(24);

            if (!squad.isNotifySent() && now.isAfter(notifiyTime.minusMinutes(15))
                    && now.isBefore(notifiyTime.plusMinutes(15))) {
                List<String> tokens = squad.getParticipants().stream().filter(p -> p.getUser().isFcmAbled())
                        .map(p -> p.getUser().getFcmToken()).toList();

                String title = squad.getTitle();
                String content = String.format("두근두근 모임 하루 전. 멤버들도 잊지않았겠죠?.",
                        squad.getTitle().substring(0, Math.max(20, squad.getTitle().length())));
                String data = squad.getPid() + ",squad";

                sendPushToClients(tokens, title, content, data);

                squad.setNotifySent(true);
                squadRepository.save(squad);
            }
        }
    }

    @Override
    public void squadBannedPartipate(Squad squad) {
        // TODO Auto-generated method stub

    }

    @Override
    public void squadRefusePartipate(Squad squad) {
        // TODO Auto-generated method stub

    }

    private String sendPushToClient(boolean isAlarm, String token, String title, String content, String data) {
        if (isAlarm && token != null) {
            try {
                return firebasePublisherUtil.postToClient(title, content, data, token);
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private List<String> sendPushToClients(List<String> tokens, String title, String content, String data) {
        try {
            MulticastMessageRepresentation message = MulticastMessageRepresentation.builder()
                    .title(title)
                    .data(data)
                    .message(data)
                    .registrationTokens(tokens).build();
            return firebasePublisherUtil.postToClients(message);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
