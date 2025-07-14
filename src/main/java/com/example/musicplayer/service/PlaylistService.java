package com.example.musicplayer.service;

import com.example.musicplayer.dto.response.PlaylistResponse;
import com.example.musicplayer.exception.AppException;
import com.example.musicplayer.exception.ErrorCode;
import com.example.musicplayer.mapper.PlaylistMapper;
import com.example.musicplayer.model.Playlist;
import com.example.musicplayer.model.User;
import com.example.musicplayer.repository.PlaylistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistMapper playlistMapper;

    public PlaylistResponse createPlaylist(String name, User user) {
        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.setOwner(user);
        playlist.setCreatedAt(LocalDateTime.now());
        return playlistMapper.toPlaylistResponse(playlistRepository.save(playlist));
    }

    public PlaylistResponse updatePlaylist(Long playlistId, String newName, User currentUser) {
        Playlist playlist = getPlaylistOwnedByUser(playlistId, currentUser);
        playlist.setName(newName);
        return playlistMapper.toPlaylistResponse(playlistRepository.save(playlist));
    }

    public void deletePlaylist(Long playlistId, User currentUser) {
        Playlist playlist = getPlaylistOwnedByUser(playlistId, currentUser);
        playlistRepository.delete(playlist);
    }

    public List<PlaylistResponse> getPlaylistsByUser(User user) {
        List<Playlist> playlists = playlistRepository.findByOwner(user);
        return playlistMapper.toPlaylistResponseList(playlists);
    }

    public PlaylistResponse getPlaylistDetail(Long playlistId, User currentUser) {
        Playlist playlist = getPlaylistOwnedByUser(playlistId, currentUser);
        return playlistMapper.toPlaylistResponse(playlist);
    }

    public Playlist getPlaylistOwnedByUser(Long playlistId, User currentUser) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!playlist.getOwner().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        return playlist;
    }
}
