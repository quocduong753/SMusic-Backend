package com.example.musicplayer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongLikeId implements Serializable {
    private Long user;
    private Long song;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SongLikeId)) return false;
        SongLikeId that = (SongLikeId) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(song, that.song);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, song);
    }
}
