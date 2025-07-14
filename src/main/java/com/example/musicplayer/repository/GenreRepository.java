package com.example.musicplayer.repository;

import com.example.musicplayer.model.Genre;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.jpa.repository.JpaRepository;

@JsonInclude
public interface GenreRepository extends JpaRepository<Genre, Long> {
}
