package com.example.musicplayer.mapper;

import com.example.musicplayer.dto.response.ArtistResponse;
import com.example.musicplayer.model.Artist;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArtistMapper {

    ArtistResponse toResponse(Artist artist);
    List<ArtistResponse> toResponseList(List<Artist> artists);
}
