package com.example.musicplayer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String coverImageUrl;

    private LocalDate releaseDate;

    @ManyToOne
    @JoinColumn(name = "artist_id")

    private Artist artist; // optional, nếu là bài hệ thống

}
