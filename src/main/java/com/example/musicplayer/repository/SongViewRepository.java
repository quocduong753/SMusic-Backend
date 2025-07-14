package com.example.musicplayer.repository;

import com.example.musicplayer.model.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface SongViewRepository extends JpaRepository<SongView, Long> {

    // 1. Bài hát vừa nghe
    @Query("""
        SELECT sv.song
        FROM SongView sv
        WHERE sv.user = :user
        AND sv.song.isUserUpload = false
        ORDER BY sv.viewedAt DESC
    """)
    List<Song> findRecentlyListenedSongs(@Param("user") User user, Pageable pageable);

    // 2. Bài hát nghe nhiều nhất bởi user
    @Query("""
        SELECT sv.song
        FROM SongView sv
        WHERE sv.user = :user
        GROUP BY sv.song
        ORDER BY COUNT(sv.id) DESC
    """)
    List<Song> findMostListenedSongsByUser(@Param("user") User user, Pageable pageable);

    // 3. Thể loại yêu thích nhất của user
    @Query("""
        SELECT sv.song.genre.id
        FROM SongView sv
        WHERE sv.user = :user
        GROUP BY sv.song.genre.id
        ORDER BY COUNT(sv.id) DESC
    """)
    List<Long> findTopGenreIdsByUser(@Param("user") User user);

    // 4. Xoá theo bài hát
    @Modifying
    @Transactional
    void deleteBySong(Song song);

    // ✅ 5. Top bài hát hệ thống tuần trước
    @Query("""
        SELECT sv.song
        FROM SongView sv
        WHERE sv.viewedAt BETWEEN :start AND :end
          AND sv.song.isPublic = true
          AND sv.song.isUserUpload = false
        GROUP BY sv.song
        ORDER BY COUNT(sv.id) DESC
    """)
    List<Song> findTopSongsLastWeek(@Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end,
                                    Pageable pageable);

    // ✅ 6. Top nghệ sĩ hệ thống tuần trước
    @Query("""
    SELECT a
    FROM SongView sv
    JOIN sv.song s
    JOIN s.artists a
    WHERE sv.viewedAt BETWEEN :start AND :end
      AND s.isPublic = true
      AND s.isUserUpload = false
    GROUP BY a
    ORDER BY COUNT(sv.id) DESC
""")
    List<Artist> findTopArtistsLastWeek(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end,
                                        Pageable pageable);

    // ✅ 7. Top album hệ thống tuần trước
    @Query("""
        SELECT s.album
        FROM SongView sv
        JOIN sv.song s
        WHERE sv.viewedAt BETWEEN :start AND :end
          AND s.isPublic = true
          AND s.isUserUpload = false
        GROUP BY s.album
        ORDER BY COUNT(sv.id) DESC
    """)
    List<Album> findTopAlbumsLastWeek(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end,
                                      Pageable pageable);

    // 8. Lịch sử nghe của user (tùy chọn lọc bài hệ thống)
    @Query(value = """
    SELECT s.*
    FROM (
        SELECT MAX(sv.viewed_at) AS latest_viewed, sv.song_id
        FROM song_view sv
        JOIN song s ON sv.song_id = s.id
        WHERE sv.user_id = :userId
          AND s.pending_review = false
          AND (:onlySystem = false OR (s.is_public = true AND s.is_user_upload = false))
        GROUP BY sv.song_id
    ) AS sub
    JOIN song s ON sub.song_id = s.id
    ORDER BY sub.latest_viewed DESC
    LIMIT :limit OFFSET :offset
""", nativeQuery = true)
    List<Song> findDistinctListeningHistory(
            @Param("userId") Long userId,
            @Param("onlySystem") boolean onlySystem,
            @Param("limit") int limit,
            @Param("offset") int offset
    );


    @Query("""
        SELECT sv.song
        FROM SongView sv
        WHERE sv.viewedAt BETWEEN :start AND :end
          AND sv.song.isUserUpload = true
          AND sv.song.isPublic = true
          AND sv.song.pendingReview = false
        GROUP BY sv.song
        ORDER BY COUNT(sv.id) DESC
    """)
    List<Song> findTopUserUploadedByViewThisWeek(@Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end,
                                                 Pageable pageable);
}

