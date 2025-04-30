package com.whdcks3.portfolio.gory_server.controllers;

import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whdcks3.portfolio.gory_server.data.requests.PushRequest;
import com.whdcks3.portfolio.gory_server.firebase.FirebasePublisherUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/push")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PushNotificationController {
    private final FirebasePublisherUtil firebasePublisherUtil;

    @PostMapping("/send")
    public String postMethodName(@RequestBody PushRequest req) {
        try {
            firebasePublisherUtil.sendPushNotification(req);
            return "Push NOtification Sent";
        } catch (ExecutionException | InterruptedException e) {
            return "Error Sending Push NotififcatioN: " + e.getMessage();
        }
    }

}
