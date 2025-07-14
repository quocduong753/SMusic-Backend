package com.example.musicplayer.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistSongRequest {
    @NotBlank(message = "INVALID_INPUT")
    private Long playlistId;
    @NotBlank(message = "INVALID_INPUT")
    private Long songId;
}
