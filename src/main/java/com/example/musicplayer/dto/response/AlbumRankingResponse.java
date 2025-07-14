package com.example.musicplayer.dto.response;

import com.example.musicplayer.model.Artist;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AlbumRankingResponse {
    private Long id;
    private String name;
    private Artist artist;
    private String coverImageUrl;
    private LocalDate releaseDate;
    private Long totalListenCount;

}
