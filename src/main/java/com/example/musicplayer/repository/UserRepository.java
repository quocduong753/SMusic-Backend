package com.example.musicplayer.repository;

import com.example.musicplayer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    @Query("""
        SELECT DISTINCT s.user FROM UserSubscription s
        WHERE s.endAt BETWEEN :yesterdayStart AND :yesterdayEnd
        AND s.user.isVip = true
        AND NOT EXISTS (
            SELECT 1 FROM UserSubscription us
            WHERE us.user = s.user AND us.endAt > :now
        )
    """)
    List<User> findUsersWhoseVipJustExpired(
            @Param("yesterdayStart") LocalDateTime yesterdayStart,
            @Param("yesterdayEnd") LocalDateTime yesterdayEnd,
            @Param("now") LocalDateTime now
    );


    @Query("""
        SELECT DISTINCT s.user FROM UserSubscription s
        WHERE s.endAt < :now
        AND s.user.isVip = true
        AND NOT EXISTS (
            SELECT 1 FROM UserSubscription us
            WHERE us.user = s.user AND us.endAt > :now
        )
    """)
    List<User> findUsersWithExpiredVipAtStartup(@Param("now") LocalDateTime now);



}