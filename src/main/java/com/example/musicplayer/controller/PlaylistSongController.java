package com.example.musicplayer.controller;

import com.example.musicplayer.dto.common.ApiResponse;
import com.example.musicplayer.dto.request.PlaylistSongRequest;
import com.example.musicplayer.dto.response.PlaylistResponse;
import com.example.musicplayer.model.User;
import com.example.musicplayer.service.PlaylistSongService;
import com.example.musicplayer.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/playlist-songs")
@RequiredArgsConstructor
public class PlaylistSongController {

    private final PlaylistSongService playlistSongService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<PlaylistResponse>> addSongToPlaylist(@RequestBody PlaylistSongRequest request) {
        User currentUser = SecurityUtil.getCurrentUser();
        PlaylistResponse response = playlistSongService.addSongToPlaylist(request, currentUser);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse<PlaylistResponse>> removeSongFromPlaylist(@RequestBody PlaylistSongRequest request) {
        User currentUser = SecurityUtil.getCurrentUser();
        PlaylistResponse response = playlistSongService.removeSongFromPlaylist(request, currentUser);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @GetMapping("/exists")
    public ResponseEntity<ApiResponse<Boolean>> isSongInPlaylist(@RequestParam Long playlistId,
                                                                 @RequestParam Long songId) {
        User currentUser = SecurityUtil.getCurrentUser();
        boolean exists = playlistSongService.isSongInPlaylist(playlistId, songId, currentUser);
        return ResponseEntity.ok(new ApiResponse<>(exists));
    }
}

