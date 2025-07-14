package com.example.musicplayer.service;

import com.example.musicplayer.enums.NotificationTargetType;
import com.example.musicplayer.model.User;
import com.example.musicplayer.model.UserDeviceToken;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FirebaseService {

    private static final String FCM_ENDPOINT = "https://fcm.googleapis.com/v1/projects/music-app-c475a/messages:send";
    private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static final String[] SCOPES = { MESSAGING_SCOPE };

    public void sendPushNotification(String fcmToken, String title, String body,
                                     NotificationTargetType targetType, Long targetId, Long notificationId) {
        try {
            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(new ClassPathResource("firebase/serviceAccountKey.json").getInputStream())
                    .createScoped(Arrays.asList(SCOPES));
            googleCredentials.refreshIfExpired();

            String accessToken = googleCredentials.getAccessToken().getTokenValue();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            Map<String, Object> notification = Map.of(
                    "title", title,
                    "body", body
            );

            Map<String, String> data = Map.of(
                    "targetType", targetType.name(),
                    "targetId", String.valueOf(targetId),
                    "notificationId", String.valueOf(notificationId)
            );

            Map<String, Object> message = Map.of(
                    "token", fcmToken,
                    "notification", notification,
                    "data", data
            );

            Map<String, Object> bodyMap = Map.of("message", message);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(bodyMap, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(FCM_ENDPOINT, entity, String.class);
            System.out.println("✅ Push response: " + response.getBody());

        } catch (Exception e) {
            System.err.println("❌ Push failed: " + e.getMessage());
        }
    }

    public void sendPushToUser(User user, String title, String body,
                               NotificationTargetType targetType, Long targetId,
                               List<UserDeviceToken> tokens, Long notificationId) {
        for (UserDeviceToken token : tokens) {
            sendPushNotification(token.getFcmToken(), title, body, targetType, targetId, notificationId);
        }
    }
}
