package com.example.musicplayer.repository;


import com.example.musicplayer.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    @Query(
            value = """
    SELECT *
    FROM artist
    WHERE LOWER(name) LIKE CONCAT('%', :keyword, '%')
    ORDER BY
      CASE
        WHEN LOWER(name) REGEXP LOWER(CONCAT('^', :keyword, '($| )')) THEN 1
        WHEN LOWER(name) REGEXP LOWER(CONCAT('(^| )', :keyword, '($| )')) THEN 2
        WHEN LOWER(name) LIKE LOWER(CONCAT(:keyword, '%')) THEN 3
        ELSE 4
      END
    LIMIT :limit
    """,
            nativeQuery = true
    )
    List<Artist> searchArtistsWithPriority(
            @Param("keyword") String keyword,
            @Param("limit") int limit
    );

}
