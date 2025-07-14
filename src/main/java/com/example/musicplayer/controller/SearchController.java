package com.example.musicplayer.controller;

import com.example.musicplayer.dto.common.ApiResponse;
import com.example.musicplayer.dto.response.AlbumResponse;
import com.example.musicplayer.dto.response.ArtistResponse;
import com.example.musicplayer.dto.response.SongResponse;
import com.example.musicplayer.model.User;
import com.example.musicplayer.service.AlbumService;
import com.example.musicplayer.service.ArtistService;
import com.example.musicplayer.service.SongService;
import com.example.musicplayer.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

  private final SongService songService;
  private final ArtistService artistService;
  private final AlbumService albumService;

  @GetMapping
  public ResponseEntity<ApiResponse<Map<String, Object>>> search(
          @RequestParam String keyword,
          @RequestParam(defaultValue = "10") int songLimit,
          @RequestParam(defaultValue = "5") int artistLimit,
          @RequestParam(defaultValue = "5") int albumLimit){

    List<SongResponse> songs = songService.searchSongs(keyword, songLimit);
    List<ArtistResponse> artists = artistService.searchArtists(keyword, artistLimit);
    List<AlbumResponse> albums = albumService.searchAlbums(keyword, albumLimit);

    Map<String, Object> result = new HashMap<>();
    result.put("songs", songs);
    result.put("artists", artists);
    result.put("albums", albums);

    return ResponseEntity.ok(new ApiResponse<>(result));
  }

  @GetMapping("/user-public")
  public ResponseEntity<ApiResponse<List<SongResponse>>> searchUserPublicSongs(
          @RequestParam String keyword,
          @RequestParam(defaultValue = "10") int limit) {
    List<SongResponse> results = songService.searchUserPublicSongs(keyword, limit);
    return ResponseEntity.ok(new ApiResponse<>(results));
  }

  @GetMapping("/my-uploads")
  public ResponseEntity<ApiResponse<List<SongResponse>>> searchMyUploadedSongs(
          @RequestParam String keyword) {
    User currentUser = SecurityUtil.getCurrentUser();
    List<SongResponse> results = songService.searchUserUploadedSongs(currentUser, keyword);
    return ResponseEntity.ok(new ApiResponse<>(results));
  }

}

