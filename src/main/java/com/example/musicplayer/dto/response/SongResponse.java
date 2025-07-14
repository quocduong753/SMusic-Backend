package com.example.musicplayer.dto.response;

import com.example.musicplayer.model.Artist;
import com.example.musicplayer.model.Genre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongResponse {
    private Long id;
    private String title;
    private String fileUrl;
    private String description;

    private List<ArtistResponse> artists;

    private String coverImageUrl;
    private AlbumResponse album;
    private boolean isUserUpload;
    private boolean isPublic;
    private boolean pendingReview = false;
    private int likeCount;
    private int listenCount;
    private LocalDate releaseDate;
    private UserResponse uploadedBy;
    private Genre genre;
    private boolean liked;
}