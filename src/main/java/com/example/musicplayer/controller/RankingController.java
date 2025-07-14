package com.example.musicplayer.controller;

import com.example.musicplayer.dto.common.ApiResponse;
import com.example.musicplayer.dto.response.AlbumResponse;
import com.example.musicplayer.dto.response.ArtistResponse;
import com.example.musicplayer.dto.response.SongResponse;
import com.example.musicplayer.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rankings")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/songs/listen-week")
    public ResponseEntity<ApiResponse<List<SongResponse>>> getTopSongsThisWeek(
            @RequestParam(defaultValue = "30") int limit) {
        return ResponseEntity.ok(new ApiResponse<>(rankingService.getTopSongsByListenThisWeek(limit)));
    }

    @GetMapping("/songs/listen-total")
    public ResponseEntity<ApiResponse<List<SongResponse>>> getTopSongsByListenCount(
            @RequestParam(defaultValue = "30") int limit) {
        return ResponseEntity.ok(new ApiResponse<>(rankingService.getTopSongsByTotalListen(limit)));
    }

    @GetMapping("/songs/likes")
    public ResponseEntity<ApiResponse<List<SongResponse>>> getTopSongsByLikes(
            @RequestParam(defaultValue = "30") int limit) {
        return ResponseEntity.ok(new ApiResponse<>(rankingService.getTopSongsByLikes(limit)));
    }

    @GetMapping("/songs/genre/{genreId}")
    public ResponseEntity<ApiResponse<List<SongResponse>>> getTopSystemSongsByGenre(
            @PathVariable Long genreId,
            @RequestParam(defaultValue = "30") int limit) {
        return ResponseEntity.ok(new ApiResponse<>(rankingService.getTopSystemSongsByGenre(genreId, limit)));
    }

    @GetMapping("/artists")
    public ResponseEntity<ApiResponse<List<ArtistResponse>>> getTopArtistsThisWeek(
            @RequestParam(defaultValue = "30") int limit) {
        return ResponseEntity.ok(new ApiResponse<>(rankingService.getTopArtistsThisWeek(limit)));
    }

    @GetMapping("/albums")
    public ResponseEntity<ApiResponse<List<AlbumResponse>>> getTopAlbumsThisWeek(
            @RequestParam(defaultValue = "30") int limit) {
        return ResponseEntity.ok(new ApiResponse<>(rankingService.getTopAlbumsThisWeek(limit)));
    }

    @GetMapping("/user/top-liked-week")
    public ResponseEntity<ApiResponse<List<SongResponse>>> getTopUserLikedSongsThisWeek(
            @RequestParam(defaultValue = "10") int limit) {
        List<SongResponse> songs = rankingService.getTopUserUploadedByLikesAllTime(limit);
        return ResponseEntity.ok(new ApiResponse<>(songs));
    }

    @GetMapping("/user/top-listened-week")
    public ResponseEntity<ApiResponse<List<SongResponse>>> getTopUserListenedSongsThisWeek(
            @RequestParam(defaultValue = "10") int limit) {
        List<SongResponse> songs = rankingService.getTopUserUploadedByListensInWeek(limit);
        return ResponseEntity.ok(new ApiResponse<>(songs));
    }

}
