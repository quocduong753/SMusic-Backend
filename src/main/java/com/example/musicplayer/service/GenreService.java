package com.example.musicplayer.service;


import com.example.musicplayer.model.Genre;
import com.example.musicplayer.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public List<Genre> getAllGenre() {
        return genreRepository.findAll();
    }
}
