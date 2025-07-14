package com.example.musicplayer.repository;

import com.example.musicplayer.enums.ReportStatus;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.model.SongReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongReportRepository extends JpaRepository<SongReport, Long> {
    void deleteAllBySong(Song song);
    Page<SongReport> findAllByStatus(ReportStatus status, Pageable pageable);

}
