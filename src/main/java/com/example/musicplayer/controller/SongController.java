package com.example.musicplayer.controller;

import com.example.musicplayer.dto.common.ApiResponse;
import com.example.musicplayer.dto.request.SongRequest;
import com.example.musicplayer.dto.request.SongVisibilityRequest;
import com.example.musicplayer.dto.response.SongResponse;
import com.example.musicplayer.model.User;
import com.example.musicplayer.service.SongService;
import com.example.musicplayer.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @PostMapping("/upload-song")
    public ResponseEntity<ApiResponse<SongResponse>> create(
            @Valid @RequestBody SongRequest request) {
        User currentUser = SecurityUtil.getCurrentUser();
        SongResponse response = songService.createSong(request, currentUser);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SongResponse>> updateSong(
            @PathVariable Long id,
            @Valid @RequestBody SongRequest request
    ) {
        User currentUser = SecurityUtil.getCurrentUser();
        SongResponse response = songService.updateSong(id, request, currentUser);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteSong(@PathVariable Long id) {
        User currentUser = SecurityUtil.getCurrentUser();
        songService.deleteSong(id, currentUser);
        return ResponseEntity.ok(new ApiResponse<>("Song deleted successfully"));
    }

    @PutMapping("/{id}/visibility")
    public ResponseEntity<ApiResponse<SongResponse>> updateSongVisibility(
            @PathVariable Long id,
            @RequestBody SongVisibilityRequest request) {
        User currentUser = SecurityUtil.getCurrentUser();
        SongResponse response = songService.updateSongVisibility(id, request, currentUser);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }


    @GetMapping("/get-songs")
    public ResponseEntity<ApiResponse<?>> getSystemSongs(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        User currentUser = SecurityUtil.getCurrentUser();

        if (page != null && size != null) {
            return ResponseEntity.ok(new ApiResponse<>(
                    songService.getSystemSongsPaged(currentUser, page, size)
            ));
        }

        return ResponseEntity.ok(new ApiResponse<>(
                songService.getAllSystemSongsResponse(currentUser)
        ));
    }

    @GetMapping("/system/random")
    public ResponseEntity<ApiResponse<List<SongResponse>>> getRandomSystemSongs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<SongResponse> songs = songService.getRandomSystemSongs(page);
        return ResponseEntity.ok(new ApiResponse<>(songs));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SongResponse>> getSongDetail(@PathVariable Long id) {
        SongResponse response = songService.getPublicSongDetail(id);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @PostMapping("/{id}/listen")
    public ResponseEntity<ApiResponse<String>> recordListen(@PathVariable Long id) {

        User currentUser = SecurityUtil.getCurrentUser();
        songService.recordListen(id, currentUser);
        return ResponseEntity.ok(new ApiResponse<>("Listen recorded"));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<String>> toggleLike(@PathVariable Long id) {
        User currentUser = SecurityUtil.getCurrentUser();
        boolean liked = songService.toggleLike(id, currentUser);
        return ResponseEntity.ok(new ApiResponse<>(liked ? "Liked" : "Unliked"));
    }

    @GetMapping("/my-uploads")
    public ResponseEntity<ApiResponse<List<SongResponse>>> getMyUploadedSongs() {
        User currentUser = SecurityUtil.getCurrentUser();
        List<SongResponse> songs = songService.getUserUploadedSongs(currentUser);
        return ResponseEntity.ok(new ApiResponse<>(songs));
    }



    @GetMapping("/{id}/related")
    public ResponseEntity<ApiResponse<List<SongResponse>>> getRelatedSongs(
            @PathVariable Long id,
            @RequestParam(defaultValue = "10") int limit) {
        List<SongResponse> related = songService.getRelatedSongs(id, limit);
        return ResponseEntity.ok(new ApiResponse<>(related));
    }

    @GetMapping("/recommend")
    public ResponseEntity<ApiResponse<List<SongResponse>>> getRecommendations(
            @RequestParam(defaultValue = "10") int limit) {
        User currentUser = SecurityUtil.getCurrentUser();
        List<SongResponse> songs = songService.recommendSongs(currentUser, limit);
        return ResponseEntity.ok(new ApiResponse<>(songs));
    }

    @GetMapping("/album/{albumId}")
    public ResponseEntity<ApiResponse<List<SongResponse>>> getSongsByAlbum(@PathVariable Long albumId) {
        List<SongResponse> songs = songService.getSongsByAlbum(albumId);
        return ResponseEntity.ok(new ApiResponse<>(songs));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<SongResponse>>> getListeningHistory(
            @RequestParam(defaultValue = "false") boolean onlySystem,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        User currentUser = SecurityUtil.getCurrentUser();
        List<SongResponse> result = songService.getListeningHistory(currentUser, onlySystem, page, size);
        return ResponseEntity.ok(new ApiResponse<>(result));
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<ApiResponse<List<SongResponse>>> getSongsByArtist(
            @PathVariable Long artistId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<SongResponse> songs = songService.getSongsByArtist(artistId, page, size);
        return ResponseEntity.ok(new ApiResponse<>(songs));
    }


    @GetMapping("/user-public-suggestions")
    public ResponseEntity<ApiResponse<List<SongResponse>>> getSuggestedUserPublicSongs(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(defaultValue = "0") int page
    ) {
        List<SongResponse> songs = songService.suggestUserUploadedSongs(limit, days,page);
        return ResponseEntity.ok(new ApiResponse<>(songs));
    }

}
