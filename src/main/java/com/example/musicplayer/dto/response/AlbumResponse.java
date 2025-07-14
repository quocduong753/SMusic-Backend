package com.example.musicplayer.dto.response;

import com.example.musicplayer.model.Artist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumResponse {
    private Long id;
    private String title;
    private String coverImageUrl;
    private LocalDate releaseDate;
    private ArtistResponse artist;
}
