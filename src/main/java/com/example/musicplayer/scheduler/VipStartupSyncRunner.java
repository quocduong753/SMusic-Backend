package com.example.musicplayer.scheduler;

import com.example.musicplayer.model.User;
import com.example.musicplayer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VipStartupSyncRunner implements ApplicationRunner {

    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) {
        LocalDateTime now = LocalDateTime.now();

        List<User> users = userRepository.findUsersWithExpiredVipAtStartup(now);

        for (User user : users) {
            if (user.isVip()) {
                user.setVip(false);
            }
        }

        userRepository.saveAll(users);
        System.out.printf("[Startup] Đồng bộ VIP: đã hạ cấp %d người dùng.%n", users.size());
    }
}
