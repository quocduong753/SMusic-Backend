package com.example.musicplayer.repository;

import com.example.musicplayer.model.Song;
import com.example.musicplayer.model.SongLike;
import com.example.musicplayer.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SongLikeRepository extends JpaRepository<SongLike, Long> {
    boolean existsByUserAndSong(User user, Song song);
    Optional<SongLike> findByUserAndSong(User user, Song song);
    @Modifying
    @Transactional
    void deleteBySong(Song song);


}