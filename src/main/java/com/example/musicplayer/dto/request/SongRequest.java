package com.example.musicplayer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongRequest {
    @NotBlank(message = "TITLE_REQUIRED")
    private String title;

    private String fileUrl;
    private String coverImageUrl;
    private String description;
    private LocalDate releaseDate;
    @NotNull(message = "GENRE_REQUIRED")
    private Long genreId;
    private Boolean isPublic;

}
