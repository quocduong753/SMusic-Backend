package com.example.musicplayer.repository;

import com.example.musicplayer.model.Artist;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByIsUserUploadFalseOrderByReleaseDateDesc();
    List<Song> findByAlbumIdOrderByReleaseDateDesc(Long albumId);
    Page<Song> findByIsUserUploadFalseOrderByReleaseDateDesc(Pageable pageable);
    List<Song> findByIsUserUploadTrueAndUploadedByAndPendingReviewFalseOrderByReleaseDateDesc(User user);
    List<Song> findAllByIsUserUploadTrueAndIsPublicTrueAndPendingReviewFalseOrderByLikeCountDesc(Pageable pageable);
    @Query("""
    SELECT s FROM Song s
    JOIN s.artists a
    WHERE a.id = :artistId
      AND s.isPublic = true
      AND s.isUserUpload = false
    ORDER BY s.releaseDate ASC
""")
    List<Song> findByArtistIdAndIsPublicTrueAndIsUserUploadFalseOrderByReleaseDateAsc(
            @Param("artistId") Long artistId,
            Pageable pageable
    );
    @Query(
            value = "SELECT * FROM song WHERE is_user_upload = false ORDER BY RAND() LIMIT :limit",
            nativeQuery = true
    )
    List<Song> findRandomSystemSongs(@Param("limit") int limit);

    @Query(
            value = """
        SELECT s.*
        FROM song s
        WHERE s.is_public = true AND LOWER(s.title) LIKE CONCAT('%', :keyword, '%')
        ORDER BY
          CASE
            WHEN LOWER(s.title) REGEXP LOWER(CONCAT('^', :keyword, '($| )')) THEN 1
            WHEN LOWER(s.title) REGEXP LOWER(CONCAT('(^| )', :keyword, '($| )')) THEN 2
            WHEN LOWER(s.title) LIKE LOWER(CONCAT(:keyword, '%')) THEN 3
            ELSE 4
          END
        LIMIT :limit
      """,
            nativeQuery = true
    )
    List<Song> searchPublicSongsByKeyword(
            @Param("keyword") String keyword,
            @Param("limit") int limit
    );

    @Query(value = """
        SELECT s.*
        FROM song s
        WHERE s.is_public = true
          AND s.is_user_upload = true
          AND LOWER(s.title) LIKE CONCAT('%', :keyword, '%')
        ORDER BY
          CASE
            WHEN LOWER(s.title) REGEXP LOWER(CONCAT('^', :keyword, '($| )')) THEN 1
            WHEN LOWER(s.title) REGEXP LOWER(CONCAT('(^| )', :keyword, '($| )')) THEN 2
            WHEN LOWER(s.title) LIKE LOWER(CONCAT(:keyword, '%')) THEN 3
            ELSE 4
          END
        LIMIT :limit
    """, nativeQuery = true)
    List<Song> searchUserUploadedPublicSongsByKeywordAccurate(
            @Param("keyword") String keyword,
            @Param("limit") int limit
    );


    @Modifying
    @Query("UPDATE Song s SET s.listenCount = s.listenCount + 1 WHERE s.id = :id")
    void incrementListenCount(@Param("id") Long id);


    @Query(
            value = """
    SELECT * FROM song s
    WHERE s.is_user_upload = true
      AND s.uploaded_by = :userId
      AND LOWER(s.title) LIKE CONCAT('%', :keyword, '%')
      AND s.pending_review = false
    ORDER BY
      CASE
        WHEN LOWER(s.title) REGEXP LOWER(CONCAT('^', :keyword, '($| )')) THEN 1
        WHEN LOWER(s.title) REGEXP LOWER(CONCAT('(^| )', :keyword, '($| )')) THEN 2
        WHEN LOWER(s.title) LIKE LOWER(CONCAT(:keyword, '%')) THEN 3
        ELSE 4
      END
""",
            nativeQuery = true
    )
    List<Song> searchUserUploadedSongsByTitleNative(
            @Param("userId") Long userId,
            @Param("keyword") String keyword
    );

    @Query("""
    SELECT DISTINCT s FROM Song s
    JOIN s.artists a
    WHERE a = :artist
      AND s.id <> :excludeId
      AND s.isPublic = true
      AND s.isUserUpload = false
    ORDER BY s.listenCount DESC
""")
    List<Song> findRelatedSongsByArtist(
            @Param("artist") Artist artist,
            @Param("excludeId") Long excludeId
    );

    @Query("""
        SELECT DISTINCT s FROM Song s
        JOIN s.artists a
        WHERE a IN :artists
          AND s.id <> :excludeId
          AND s.isPublic = true
          AND s.isUserUpload = false
        ORDER BY s.listenCount DESC
    """)
    List<Song> findRelatedSongsByAnyArtist(@Param("artists") List<Artist> artists, @Param("excludeId") Long excludeId);


    @Query("""
    SELECT s FROM Song s
    WHERE s.genre = :genre
     AND s.id <> :excludeId
     AND s.isPublic = true
     AND s.isUserUpload = false
    ORDER BY s.listenCount DESC
""")
    List<Song> findRelatedSongsByGenre(
            @Param("genre") com.example.musicplayer.model.Genre genre,
            @Param("excludeId") Long excludeId,
            Pageable pageable
    );

    // Các bài do cùng người dùng upload, không phải bài gốc, đã public và không pending
    @Query("""
    SELECT s FROM Song s
    WHERE s.uploadedBy.id = :uploaderId
      AND s.id <> :excludeId
      AND s.isPublic = true
      AND s.pendingReview = false
      AND s.isUserUpload = true
    ORDER BY s.listenCount DESC
""")
    List<Song> findRelatedUserUploadedSongsByUploader(
            @Param("uploaderId") Long uploaderId,
            @Param("excludeId") Long excludeId,
            Pageable pageable
    );

    // Các bài cùng thể loại do người khác upload, đã public và không pending
    @Query("""
    SELECT s FROM Song s
    WHERE s.genre.id = :genreId
      AND s.uploadedBy.id <> :uploaderId
      AND s.id <> :excludeId
      AND s.isPublic = true
      AND s.pendingReview = false
      AND s.isUserUpload = true
    ORDER BY s.listenCount DESC
""")
    List<Song> findRelatedUserUploadedSongsByGenreExceptUploader(
            @Param("genreId") Long genreId,
            @Param("uploaderId") Long uploaderId,
            @Param("excludeId") Long excludeId,
            Pageable pageable
    );

    @Query("""
    SELECT s FROM Song s
    WHERE s.genre.id IN :genreIds AND s.isPublic = true AND s.isUserUpload = false
    """)
    List<Song> findRecommendedByGenres(@Param("genreIds") List<Long> genreIds);

    @Query("""
    SELECT s FROM Song s
    WHERE s.isPublic = true AND s.isUserUpload = false
    ORDER BY s.listenCount DESC
""")
    List<Song> findTopByIsPublicTrueAndIsUserUploadFalseOrderByListenCountDesc(Pageable pageable);

    @Query("""
    SELECT s FROM Song s
    WHERE s.isPublic = true AND s.isUserUpload = false
    ORDER BY s.likeCount DESC
""")
    List<Song> findTopByIsPublicTrueAndIsUserUploadFalseOrderByLikeCountDesc(Pageable pageable);

    @Query("""
    SELECT s FROM Song s
    WHERE s.genre.id = :genreId AND s.isPublic = true AND s.isUserUpload = false
    ORDER BY s.listenCount DESC
""")
    List<Song> findTopByGenreIdAndIsPublicTrueAndIsUserUploadFalseOrderByListenCountDesc(
            @Param("genreId") Long genreId,
            Pageable pageable
    );

    // Lấy top bài theo lượt nghe
    @Query(value = """
    SELECT * FROM song
    WHERE is_user_upload = true
      AND is_public = true
      AND release_date >= :cutoffDate
      AND pending_review = false
    ORDER BY listen_count DESC
    LIMIT :limit
""", nativeQuery = true)
    List<Song> findTopUserUploadedByListens(@Param("cutoffDate") LocalDate cutoffDate, @Param("limit") int limit);

    // Lấy top bài theo lượt thích
    @Query(value = """
    SELECT * FROM song
    WHERE is_user_upload = true
      AND is_public = true
      AND release_date >= :cutoffDate
    ORDER BY like_count DESC
    LIMIT :limit
""", nativeQuery = true)
    List<Song> findTopUserUploadedByLikes(@Param("cutoffDate") LocalDate cutoffDate, @Param("limit") int limit);


    //bài mới trong ngày gần đây
    @Query(value = """
    SELECT * FROM song
    WHERE is_user_upload = true
      AND is_public = true
      AND s.pending_review = false
    ORDER BY release_date DESC
    LIMIT :limit
""", nativeQuery = true)
    List<Song> findRecentUserUploadedSongs(@Param("limit") int limit);

    @Query("""
    SELECT s FROM Song s
    WHERE s.uploadedBy.id = :userId
      AND s.releaseDate >= :cutoffDate
      AND s.isPublic = true
      AND s.isUserUpload = true
      AND s.pendingReview = false
    ORDER BY s.releaseDate DESC
""")
    List<Song> findRecentSongsByUser(
            @Param("userId") Long userId,
            @Param("cutoffDate") LocalDate cutoffDate,
            Pageable pageable
    );

}
