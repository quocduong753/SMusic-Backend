package com.example.musicplayer.controller;

import com.example.musicplayer.dto.common.ApiResponse;
import com.example.musicplayer.dto.request.PlaylistRequest;
import com.example.musicplayer.dto.response.PlaylistResponse;
import com.example.musicplayer.model.User;
import com.example.musicplayer.service.PlaylistService;
import com.example.musicplayer.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping
    public ResponseEntity<ApiResponse<PlaylistResponse>> createPlaylist(@RequestBody PlaylistRequest request) {
        User currentUser = SecurityUtil.getCurrentUser();
        PlaylistResponse response = playlistService.createPlaylist(request.getName(), currentUser);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PlaylistResponse>> updatePlaylist(@PathVariable Long id,
                                                                        @RequestBody PlaylistRequest request) {
        User currentUser = SecurityUtil.getCurrentUser();
        PlaylistResponse response = playlistService.updatePlaylist(id, request.getName(), currentUser);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePlaylist(@PathVariable Long id) {
        User currentUser = SecurityUtil.getCurrentUser();
        playlistService.deletePlaylist(id, currentUser);
        return ResponseEntity.ok(new ApiResponse<>(null)); // giữ chuẩn cấu trúc
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PlaylistResponse>>> getUserPlaylists() {
        User currentUser = SecurityUtil.getCurrentUser();
        List<PlaylistResponse> responses = playlistService.getPlaylistsByUser(currentUser);
        return ResponseEntity.ok(new ApiResponse<>(responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlaylistResponse>> getPlaylistDetail(@PathVariable Long id) {
        User currentUser = SecurityUtil.getCurrentUser();
        PlaylistResponse response = playlistService.getPlaylistDetail(id, currentUser);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }
}
