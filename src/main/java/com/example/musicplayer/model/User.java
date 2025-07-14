package com.example.musicplayer.model;

import com.example.musicplayer.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "uploadedSongs")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String name;

    private boolean isVip = false;

    private String gender;

    private LocalDate birthDate;

    @OneToMany(mappedBy = "uploadedBy", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("uploadedBy")
    private List<Song> uploadedSongs;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;
}