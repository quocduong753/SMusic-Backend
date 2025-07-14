package com.example.musicplayer.service;

import com.example.musicplayer.dto.response.AlbumResponse;
import com.example.musicplayer.dto.response.ArtistResponse;
import com.example.musicplayer.dto.response.SongResponse;
import com.example.musicplayer.mapper.AlbumMapper;
import com.example.musicplayer.mapper.ArtistMapper;
import com.example.musicplayer.mapper.SongMapper;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.model.User;
import com.example.musicplayer.repository.SongLikeRepository;
import com.example.musicplayer.repository.SongRepository;
import com.example.musicplayer.repository.SongViewRepository;
import com.example.musicplayer.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {
    private final SongRepository songRepository;
    private final SongViewRepository songViewRepository;
    private final SongMapper songMapper;
    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;
    private final SongLikeRepository songLikeRepository;

    /**
     * Trả về mốc thời gian từ thứ Hai đến Chủ Nhật của tuần trước
     */
    public LocalDateTime[] getLastWeekRange() {
        LocalDate today = LocalDate.now();
        LocalDate lastMonday = today.minusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDate lastSunday = lastMonday.with(DayOfWeek.SUNDAY);
        return new LocalDateTime[] {
                lastMonday.atStartOfDay(),
                lastSunday.atTime(LocalTime.MAX)
        };
    }

    /**
     * Xếp hạng: bài hát hệ thống có lượt nghe cao nhất trong tuần trước
     */
    public List<SongResponse> getTopSongsByListenThisWeek(int limit) {
        LocalDateTime[] range = getLastWeekRange();
        return songViewRepository.findTopSongsLastWeek(range[0], range[1], PageRequest.of(0, limit))
                .stream()
                .map(this::enrichSongResponse)
                .toList();
    }

    /**
     * Xếp hạng: bài hát hệ thống có lượt nghe cao nhất tổng cộng
     */
    public List<SongResponse> getTopSongsByTotalListen(int limit) {
        return songRepository.findTopByIsPublicTrueAndIsUserUploadFalseOrderByListenCountDesc(PageRequest.of(0, limit))
                .stream()
                .map(this::enrichSongResponse)
                .toList();
    }

    /**
     * Xếp hạng: bài hát hệ thống có nhiều lượt thích nhất
     */
    public List<SongResponse> getTopSongsByLikes(int limit) {
        return songRepository.findTopByIsPublicTrueAndIsUserUploadFalseOrderByLikeCountDesc(PageRequest.of(0, limit))
                .stream()
                .map(this::enrichSongResponse)
                .toList();
    }

    /**
     * Xếp hạng: bài hát hệ thống theo thể loại
     */
    public List<SongResponse> getTopSystemSongsByGenre(Long genreId, int limit) {
        return songRepository.findTopByGenreIdAndIsPublicTrueAndIsUserUploadFalseOrderByListenCountDesc(genreId, PageRequest.of(0, limit))
                .stream()
                .map(this::enrichSongResponse)
                .toList();
    }

    /**
     * Xếp hạng: nghệ sĩ được nghe nhiều nhất tuần trước
     */
    public List<ArtistResponse> getTopArtistsThisWeek(int limit) {
        LocalDateTime[] range = getLastWeekRange();
        return songViewRepository.findTopArtistsLastWeek(range[0], range[1], PageRequest.of(0, limit))
                .stream()
                .map(artistMapper::toResponse)
                .toList();
    }

    /**
     * Xếp hạng: album được nghe nhiều nhất tuần trước
     */
    public List<AlbumResponse> getTopAlbumsThisWeek(int limit) {
        LocalDateTime[] range = getLastWeekRange();
        return songViewRepository.findTopAlbumsLastWeek(range[0], range[1], PageRequest.of(0, limit))
                .stream()
                .map(albumMapper::toAlbumResponse)
                .toList();
    }

    public List<SongResponse> getTopUserUploadedByListensInWeek(int limit) {
        LocalDateTime[] range = getLastWeekRange();

        List<Song> songs = songViewRepository.findTopUserUploadedByViewThisWeek(range[0], range[1], PageRequest.of(0, limit));
        return songs.stream()
                .map(this::enrichSongResponse)
                .toList();
    }


    public List<SongResponse> getTopUserUploadedByLikesAllTime(int limit) {
        List<Song> songs = songRepository.findAllByIsUserUploadTrueAndIsPublicTrueAndPendingReviewFalseOrderByLikeCountDesc(PageRequest.of(0, limit));
        return songs.stream()
                .map(this::enrichSongResponse)
                .toList();
    }


    /**
     * Gắn thông tin "đã thích" vào phản hồi bài hát
     */
    private SongResponse enrichSongResponse(Song song) {
        User currentUser = SecurityUtil.getCurrentUser();
        SongResponse response = songMapper.toSongResponse(song);
        if (currentUser != null) {
            boolean liked = songLikeRepository.existsByUserAndSong(currentUser, song);
            response.setLiked(liked);
        } else {
            response.setLiked(false);
        }
        return response;
    }
}

