package com.example.musicplayer.controller;

import com.example.musicplayer.dto.common.ApiResponse;
import com.example.musicplayer.dto.response.AlbumResponse;
import com.example.musicplayer.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<ApiResponse<List<AlbumResponse>>> getAlbumsByArtist(
            @PathVariable Long artistId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<AlbumResponse> albums = albumService.getAlbumsByArtist(artistId, page, size);
        return ResponseEntity.ok(new ApiResponse<>(albums));
    }
}
