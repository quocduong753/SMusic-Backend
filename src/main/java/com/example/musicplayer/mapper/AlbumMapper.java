package com.example.musicplayer.mapper;

import com.example.musicplayer.dto.response.AlbumResponse;
import com.example.musicplayer.model.Album;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AlbumMapper.class})
public interface AlbumMapper {
    AlbumResponse toAlbumResponse(Album album);
}
