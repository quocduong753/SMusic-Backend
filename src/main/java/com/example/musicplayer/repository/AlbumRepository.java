package com.example.musicplayer.repository;

import com.example.musicplayer.model.Album;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByArtistIdOrderByReleaseDateDesc(Long artistId, Pageable pageable);
    @Query(value = """
        SELECT *
        FROM album
        WHERE LOWER(title) LIKE CONCAT('%', :keyword, '%')
        ORDER BY
          CASE
            WHEN LOWER(title) REGEXP LOWER(CONCAT('^', :keyword, '($| )')) THEN 1
            WHEN LOWER(title) REGEXP LOWER(CONCAT('(^| )', :keyword, '($| )')) THEN 2
            WHEN LOWER(title) LIKE LOWER(CONCAT(:keyword, '%')) THEN 3
            ELSE 4
          END
        LIMIT :limit
    """, nativeQuery = true)
    List<Album> searchAlbumsWithPriority(
            @Param("keyword") String keyword,
            @Param("limit") int limit
    );

}
