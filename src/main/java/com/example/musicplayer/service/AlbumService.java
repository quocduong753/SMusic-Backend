package com.example.musicplayer.service;

import com.example.musicplayer.dto.response.AlbumResponse;
import com.example.musicplayer.mapper.AlbumMapper;
import com.example.musicplayer.model.Album;
import com.example.musicplayer.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;

    public List<AlbumResponse> searchAlbums(String keyword, int limit) {
        keyword = keyword.toLowerCase();
        List<Album> albums = albumRepository.searchAlbumsWithPriority(keyword, limit);
        return albums.stream()
                .map(albumMapper::toAlbumResponse)
                .toList();
    }

    public List<AlbumResponse> getAlbumsByArtist(Long artistId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Album> albums = albumRepository.findByArtistIdOrderByReleaseDateDesc(artistId, pageable);
        return albums.stream()
                .map(albumMapper::toAlbumResponse)
                .toList();
    }
}
