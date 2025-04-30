package com.whdcks3.portfolio.gory_server.firebase;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.whdcks3.portfolio.gory_server.data.requests.PushRequest;
import com.whdcks3.portfolio.gory_server.firebase.representation.ConditionalMessageRepresentation;
import com.whdcks3.portfolio.gory_server.firebase.representation.MulticastMessageRepresentation;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FirebasePublisherUtil {
        private final FirebaseMessaging fcm;

        public String postToTopic(PushRequest req, String topic) throws FirebaseMessagingException {
                Notification notification = Notification.builder()
                                .setTitle(req.getTitle())
                                .setBody(req.getBody())
                                .build();

                Message msg = Message.builder()
                                .setTopic(topic)
                                .setNotification(notification)
                                .putData("link", req.getLink())
                                .putData("datetime", new SimpleDateFormat("yyyy.MM.dd HH시").format(new Date()))
                                .build();

                String id = fcm.send(msg);
                return id;
        }

        public String postToCondition(ConditionalMessageRepresentation message) throws FirebaseMessagingException {
                Message msg = Message.builder()
                                .setCondition(message.getCondition())
                                .putData("body", message.getData())
                                .build();

                String id = fcm.send(msg);
                return id;
        }

        public String postToClient(String title, String message, String data, String registrationToken)
                        throws FirebaseMessagingException {
                String[] pushData = data.split(",");
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("type", pushData[1]);
                dataMap.put("pid", pushData[0]);
                Notification notification = Notification.builder()
                                .setTitle(title)
                                .setBody(message)
                                .build();
                Message msg = Message.builder()
                                .setToken(registrationToken)
                                .setNotification(notification)
                                .putAllData(dataMap)
                                .build();

                String id = fcm.send(msg);
                System.out.println("[!] push sent");
                return id;
        }

        public List<String> postToClients(MulticastMessageRepresentation message) throws FirebaseMessagingException {
                String[] pushData = message.getData().split(",");
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("type", pushData[1]);
                dataMap.put("pid", pushData[0]);
                Notification notification = Notification.builder()
                                .setTitle(message.getTitle())
                                .setBody(message.getMessage())
                                .build();
                MulticastMessage msg = MulticastMessage.builder()
                                .setNotification(notification)
                                .addAllTokens(message.getRegistrationTokens())
                                .putData("body", message.getData())
                                .build();

                BatchResponse response = fcm.sendMulticast(msg);

                List<String> ids = response.getResponses()
                                .stream()
                                .map(r -> r.getMessageId())
                                .collect(Collectors.toList());

                return ids;
        }

        public void createSubscription(String topic, @RequestBody List<String> registrationTokens)
                        throws FirebaseMessagingException {
                fcm.subscribeToTopic(registrationTokens, topic);
        }

        public void deleteSubscription(String topic, String registrationToken)
                        throws FirebaseMessagingException {
                fcm.unsubscribeFromTopic(Arrays.asList(registrationToken), topic);
        }

        // for TESTING only
        public void sendPushNotification(PushRequest req)
                        throws ExecutionException, InterruptedException {
                Message message = Message.builder()
                                .setToken(req.getFcmToken())
                                .setNotification(Notification.builder()
                                                .setTitle(req.getTitle())
                                                .setBody(req.getBody() + "|" + req.getLink())
                                                .build())
                                .build();

                String response = FirebaseMessaging.getInstance().sendAsync(message).get();
                System.out.println("FCM Push Response: " + response);
        }
}
