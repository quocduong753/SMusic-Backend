package com.example.musicplayer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String fileUrl;
    private String coverImageUrl;
    private String description;
    private boolean isPublic = false;
    private boolean isUserUpload = false;
    private boolean pendingReview = false;

    private int listenCount = 0;
    private int likeCount = 0;

    private LocalDate releaseDate;

    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;

    @ManyToOne
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    @ManyToOne
    @JsonIgnoreProperties("uploadedSongs")
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    // ✅ Thay thế quan hệ 1-nhạc sĩ thành n-nhạc sĩ
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "song_artist",
            joinColumns = @JoinColumn(name = "song_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    @JsonIgnoreProperties("songs")
    private List<Artist> artists = new ArrayList<>();
}
