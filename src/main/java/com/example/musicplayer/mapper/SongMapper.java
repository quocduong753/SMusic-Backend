package com.example.musicplayer.mapper;

import com.example.musicplayer.dto.request.SongRequest;
import com.example.musicplayer.dto.response.SongResponse;
import com.example.musicplayer.model.Song;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {ArtistMapper.class, UserMapper.class, AlbumMapper.class}
)
public interface SongMapper {

    Song toSong(SongRequest request);
    @Mapping(source = "artists", target = "artists")
    SongResponse toSongResponse(Song song);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSongFromRequest(SongRequest request, @MappingTarget Song song);
}