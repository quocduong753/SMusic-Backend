package com.example.musicplayer.service;

import com.example.musicplayer.dto.request.HandleReportRequest;
import com.example.musicplayer.dto.request.NotificationRequest;
import com.example.musicplayer.dto.request.ReportSongRequest;
import com.example.musicplayer.dto.response.ReportSongResponse;
import com.example.musicplayer.enums.NotificationTargetType;
import com.example.musicplayer.enums.NotificationType;
import com.example.musicplayer.enums.ReportStatus;
import com.example.musicplayer.exception.AppException;
import com.example.musicplayer.exception.ErrorCode;
import com.example.musicplayer.mapper.SongReportMapper;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.model.SongReport;
import com.example.musicplayer.model.User;
import com.example.musicplayer.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SongReportService {
    private final SongReportRepository songReportRepository;
    private final SongRepository songRepository;
    private final SongReportMapper songReportMapper;
    private final SongViewRepository songViewRepository;
    private final SongLikeRepository songLikeRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final NotificationService notificationService;

    public ReportSongResponse reportSong(User reporter, Long songId, ReportSongRequest request) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new AppException(ErrorCode.SONG_NOT_FOUND));

        SongReport report = songReportMapper.toSongReport(request);
        report.setSong(song);
        report.setReporter(reporter);
        report.setByAI(false);
        report.setReportedAt(LocalDateTime.now());

        SongReport saved = songReportRepository.save(report);
        return songReportMapper.toReportSongResponse(saved);
    }

    public Page<ReportSongResponse> getAllReports(Pageable pageable) {
        Page<SongReport> reports = songReportRepository.findAll(pageable);
        return reports.map(songReportMapper::toReportSongResponse);
    }

    public ReportSongResponse getReportById(Long id) {
        SongReport report = songReportRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND));
        return songReportMapper.toReportSongResponse(report);
    }

    @Transactional
    public ReportSongResponse handleReport(Long reportId, HandleReportRequest request) {
        SongReport report = songReportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND));

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new AppException(ErrorCode.REPORT_ALREADY_HANDLED);
        }

        report.setStatus(request.getStatus());
        songReportRepository.save(report);

        Song song = report.getSong();

        if (request.getStatus() == ReportStatus.RESOLVED) {

            if (song.getUploadedBy() != null) {
                NotificationRequest notify = new NotificationRequest();
                notify.setTitle("Bài hát đã bị xoá");
                notify.setMessage("Bài hát '" + song.getTitle() + "' đã bị xoá do vi phạm nội dung.");
                notify.setType(NotificationType.SONG_VIOLATION); // ✅ loại riêng biệt
                notify.setTargetType(NotificationTargetType.NONE);
                notify.setTargetId(song.getId());

                notificationService.createNotification(song.getUploadedBy(), notify);
            }

            songViewRepository.deleteBySong(song);
            songLikeRepository.deleteBySong(song);
            songReportRepository.deleteAllBySong(song);
            playlistSongRepository.deleteAllBySong(song);
            songRepository.delete(song);
        }

        return songReportMapper.toReportSongResponse(report);
    }

    public Page<ReportSongResponse> getReportsByStatus(ReportStatus status, Pageable pageable) {
        return songReportRepository.findAllByStatus(status, pageable)
                .map(songReportMapper::toReportSongResponse);
    }
}

