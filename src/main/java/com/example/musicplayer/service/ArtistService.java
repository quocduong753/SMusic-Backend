package com.example.musicplayer.service;

import com.example.musicplayer.dto.response.ArtistResponse;
import com.example.musicplayer.mapper.ArtistMapper;
import com.example.musicplayer.model.Artist;
import com.example.musicplayer.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ArtistService {
    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;

    public List<ArtistResponse> searchArtists(String keyword, int limit) {
        keyword = keyword.toLowerCase();
        return artistRepository.searchArtistsWithPriority(keyword, limit).stream()
                .map(artistMapper::toResponse)
                .toList();
    }
}
