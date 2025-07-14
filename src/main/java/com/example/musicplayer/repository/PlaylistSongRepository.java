package com.example.musicplayer.repository;

import com.example.musicplayer.model.Playlist;
import com.example.musicplayer.model.PlaylistSong;
import com.example.musicplayer.model.PlaylistSongId;
import com.example.musicplayer.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, PlaylistSongId> {
    boolean existsByPlaylistAndSong(Playlist playlist, Song song);
    Optional<PlaylistSong> findByPlaylistAndSong(Playlist playlist, Song song);
    void deleteAllBySong(Song song);
}
