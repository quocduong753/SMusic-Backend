package com.example.musicplayer.mapper;

import com.example.musicplayer.dto.request.ReportSongRequest;
import com.example.musicplayer.dto.response.ReportSongResponse;
import com.example.musicplayer.model.SongReport;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {SongMapper.class, UserMapper.class})
public interface SongReportMapper {

    SongReport toSongReport(ReportSongRequest request);

    ReportSongResponse toReportSongResponse(SongReport report);
}
