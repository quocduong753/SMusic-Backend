package com.example.musicplayer.service;

import com.example.musicplayer.dto.request.PlaylistSongRequest;
import com.example.musicplayer.dto.response.PlaylistResponse;
import com.example.musicplayer.exception.AppException;
import com.example.musicplayer.exception.ErrorCode;
import com.example.musicplayer.mapper.PlaylistMapper;
import com.example.musicplayer.model.Playlist;
import com.example.musicplayer.model.PlaylistSong;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.model.User;
import com.example.musicplayer.repository.PlaylistRepository;
import com.example.musicplayer.repository.PlaylistSongRepository;
import com.example.musicplayer.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
    public class PlaylistSongService {

    private final SongRepository songRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final PlaylistMapper playlistMapper;
    private final PlaylistRepository playlistRepository;
    private final PlaylistService playlistService;

    public PlaylistResponse addSongToPlaylist(PlaylistSongRequest request, User currentUser) {
        Playlist playlist = playlistService.getPlaylistOwnedByUser(request.getPlaylistId(), currentUser);

        Song song = songRepository.findById(request.getSongId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        if (playlistSongRepository.existsByPlaylistAndSong(playlist, song)) {
            throw new AppException(ErrorCode.DUPLICATE_RESOURCE);
        }

        PlaylistSong playlistSong = new PlaylistSong();
        playlistSong.setPlaylist(playlist);
        playlistSong.setSong(song);
        playlistSongRepository.save(playlistSong);

        Playlist updated = playlistRepository.findById(playlist.getId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        return playlistMapper.toPlaylistResponse(updated);
    }

    public PlaylistResponse removeSongFromPlaylist(PlaylistSongRequest request, User currentUser) {
        Playlist playlist = playlistService.getPlaylistOwnedByUser(request.getPlaylistId(), currentUser);

        Song song = songRepository.findById(request.getSongId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        PlaylistSong playlistSong = playlistSongRepository.findByPlaylistAndSong(playlist, song)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        playlistSongRepository.delete(playlistSong);

        Playlist updated = playlistRepository.findById(playlist.getId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        return playlistMapper.toPlaylistResponse(updated);
    }



    public boolean isSongInPlaylist(Long playlistId, Long songId, User currentUser) {
        Playlist playlist = playlistService.getPlaylistOwnedByUser(playlistId, currentUser);
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        return playlistSongRepository.existsByPlaylistAndSong(playlist, song);
    }
}
