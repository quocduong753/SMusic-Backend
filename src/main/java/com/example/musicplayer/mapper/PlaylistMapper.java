package com.example.musicplayer.mapper;

import com.example.musicplayer.dto.response.PlaylistResponse;
import com.example.musicplayer.dto.response.SongResponse;
import com.example.musicplayer.model.Playlist;
import com.example.musicplayer.model.PlaylistSong;
import com.example.musicplayer.model.Song;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = SongMapper.class)
public interface PlaylistMapper {

    @Mapping(target = "songs", source = "songs")
    PlaylistResponse toPlaylistResponse(Playlist playlist);
    List<PlaylistResponse> toPlaylistResponseList(List<Playlist> playlists);

//     Thêm phương thức hỗ trợ cho MapStruct biết cách map
    @Mapping(target = ".", source = "song")
    SongResponse playlistSongToSongResponse(PlaylistSong playlistSong);


}