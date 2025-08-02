package com.example.musicplayer.service;

import com.example.musicplayer.dto.request.NotificationRequest;
import com.example.musicplayer.dto.request.SongRequest;
import com.example.musicplayer.dto.request.SongVisibilityRequest;
import com.example.musicplayer.dto.response.PagedResponse;
import com.example.musicplayer.dto.response.SongResponse;
import com.example.musicplayer.enums.NotificationTargetType;
import com.example.musicplayer.enums.NotificationType;
import com.example.musicplayer.exception.AppException;
import com.example.musicplayer.exception.ErrorCode;
import com.example.musicplayer.mapper.SongMapper;
import com.example.musicplayer.model.*;
import com.example.musicplayer.repository.GenreRepository;
import com.example.musicplayer.repository.SongLikeRepository;
import com.example.musicplayer.repository.SongRepository;
import com.example.musicplayer.repository.SongViewRepository;
import com.example.musicplayer.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongService {
    private final SongRepository songRepository;
    private final SongMapper songMapper;
    private final SongViewRepository songViewRepository;
    private final GenreRepository genreRepository;
    private final SongLikeRepository songLikeRepository;
    private final NotificationService notificationService;
    private final SupabaseStorageService supabaseStorageService;
    private final HlsService hlsService;

    private SongResponse enrichSongResponse(Song song, User currentUser) {
        SongResponse response = songMapper.toSongResponse(song);
        if (currentUser != null) {
            boolean liked = songLikeRepository.existsByUserAndSong(currentUser, song);
            response.setLiked(liked);
        } else {
            response.setLiked(false);
        }
        return response;
    }

    public SongResponse handleUpload(SongRequest songRequest,
                                     User currentUser,
                                     MultipartFile audioFile,
                                     MultipartFile coverImage) {
        Path hlsFolder = null;
        File tempImage = null;

        try {
            hlsFolder = hlsService.convertToHls(songRequest.getTitle(), audioFile);

            String hlsUrl = supabaseStorageService.uploadHlsFolder(hlsFolder, songRequest.getTitle());

            tempImage = File.createTempFile("cover-", ".jpg");
            coverImage.transferTo(tempImage);
            String coverUrl = supabaseStorageService.uploadCoverImage(tempImage, songRequest.getTitle());

            songRequest.setFileUrl(hlsUrl);
            songRequest.setCoverImageUrl(coverUrl);

            return createSong(songRequest, currentUser);
        } catch (Exception e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            if (hlsFolder != null) {
                try {
                    hlsService.deleteDirectoryIfExists(hlsFolder);
                } catch (Exception ex) {
                    System.err.println("⚠️ Không thể xoá thư mục tạm: " + hlsFolder);
                    ex.printStackTrace();
                }
            }

            // 7. Xoá file ảnh tạm
            if (tempImage != null && tempImage.exists()) {
                tempImage.delete();
            }
        }
    }


    public SongResponse createSong(SongRequest request, User currentUser) {
        Song song = songMapper.toSong(request);
        song.setUserUpload(true);
        song.setListenCount(0);
        song.setLikeCount(0);
        song.setUploadedBy(currentUser);

        Genre genre = genreRepository.findById(request.getGenreId())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_INPUT));
        song.setGenre(genre);

        // ✅ Logic phân quyền công khai
        if (Boolean.TRUE.equals(request.getIsPublic())) {
            if (currentUser.isVip()) {
                song.setPublic(true); // Cho phép VIP công khai
            } else {
                throw new AppException(ErrorCode.UNAUTHORIZED); // Không phải VIP, không cho phép công khai
            }
        } else {
            song.setPublic(false); // riêng tư hoặc mặc định
        }

        Song saved = songRepository.save(song);
        return songMapper.toSongResponse(saved);
    }


    @Transactional
    public SongResponse updateSong(Long songId, SongRequest request, User currentUser, MultipartFile coverImage) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!song.isUserUpload() || song.getUploadedBy() == null || !song.getUploadedBy().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Cập nhật thông tin từ request
        songMapper.updateSongFromRequest(request, song);

        // Cập nhật thể loại
        Genre genre = genreRepository.findById(request.getGenreId())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_INPUT));
        song.setGenre(genre);

        // ✅ Nếu có ảnh bìa mới: xoá ảnh cũ và upload ảnh mới
        if (coverImage != null && !coverImage.isEmpty()) {
            // Xoá ảnh cũ nếu có
            String oldCoverUrl = song.getCoverImageUrl();
            if (oldCoverUrl != null && oldCoverUrl.contains("/cover/")) {
                String filename = oldCoverUrl.substring(oldCoverUrl.lastIndexOf("/") + 1);
                supabaseStorageService.deleteCoverImage(filename);
            }

            try {
                File tempImage = File.createTempFile("cover-", ".jpg");
                coverImage.transferTo(tempImage);
                String newCoverUrl = supabaseStorageService.uploadCoverImage(tempImage, song.getTitle());
                song.setCoverImageUrl(newCoverUrl);
            } catch (IOException e) {
                throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }

        return songMapper.toSongResponse(songRepository.save(song));
    }


    @Transactional
    public void deleteSong(Long songId, User currentUser) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!song.isUserUpload() || song.getUploadedBy() == null || !song.getUploadedBy().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Xóa like và view liên quan
        songLikeRepository.deleteBySong(song);
        songViewRepository.deleteBySong(song);

        songRepository.delete(song);
    }

    @Transactional
    public SongResponse updateSongVisibility(Long songId, SongVisibilityRequest request, User currentUser) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!song.isUserUpload() || song.getUploadedBy() == null ||
                !song.getUploadedBy().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (request.isNewPublic() && !currentUser.isVip()) {
            throw new AppException(ErrorCode.NEED_VIP_TO_SHARE);
        }
        song.setPublic(request.isNewPublic());
        return songMapper.toSongResponse(songRepository.save(song));
    }


    public PagedResponse<SongResponse> getSystemSongsPaged(User currentUser, int page, int size) {
        Page<Song> songPage = songRepository.findByIsUserUploadFalseOrderByReleaseDateDesc(PageRequest.of(page, size));

        List<SongResponse> content = songPage.getContent().stream()
                .map(song -> enrichSongResponse(song, currentUser))
                .toList();

        return new PagedResponse<>(
                content,
                songPage.getNumber(),
                songPage.getSize(),
                songPage.getTotalElements(),
                songPage.getTotalPages(),
                songPage.isLast()
        );
    }

    public List<SongResponse> getAllSystemSongsResponse(User currentUser) {
        return songRepository.findByIsUserUploadFalseOrderByReleaseDateDesc()
                .stream()
                .map(song -> enrichSongResponse(song, currentUser))
                .toList();
    }

    public List<SongResponse> getRandomSystemSongs(int size) {
        User currentUser = SecurityUtil.getCurrentUser();
        List<Song> songs = songRepository.findRandomSystemSongs(size);
        return songs.stream()
                .map(song -> enrichSongResponse(song, currentUser))
                .toList();
    }

    public List<SongResponse> searchSongs(String keyword, int limit) {
        keyword = keyword.toLowerCase();
        User currentUser = SecurityUtil.getCurrentUser();

        return songRepository.searchPublicSongsByKeyword(keyword, limit).stream()
                .map(song -> enrichSongResponse(song, currentUser))
                .toList();
    }

    public List<SongResponse> searchUserPublicSongs(String keyword, int limit) {
        keyword = keyword.toLowerCase();
        User currentUser = SecurityUtil.getCurrentUser();
        return songRepository.searchUserUploadedPublicSongsByKeywordAccurate(keyword, limit)
                .stream()
                .map(song->enrichSongResponse(song, currentUser))
                .toList();
    }


    public SongResponse getPublicSongDetail(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        User currentUser = SecurityUtil.getCurrentUser();


        return enrichSongResponse(song, currentUser);
    }
    @Transactional
    public void recordListen(Long songId, User user) {
        try {
            // 1. Tăng lượt nghe
            songRepository.incrementListenCount(songId);

            // 2. Ghi nhận lịch sử nếu có người dùng
            if (user != null) {
                Song song = new Song();
                song.setId(songId);

                SongView view = new SongView();
                view.setSong(song);
                view.setUser(user);
                view.setViewedAt(LocalDateTime.now());

                songViewRepository.save(view);
            }
        } catch (Exception ex) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public boolean toggleLike(Long songId, User user) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        Optional<SongLike> existingLike = songLikeRepository.findByUserAndSong(user, song);

        if (existingLike.isPresent()) {
            songLikeRepository.delete(existingLike.get());
            song.setLikeCount(song.getLikeCount() - 1);
            songRepository.save(song);
            return false;
        } else {
            SongLike like = new SongLike();
            like.setUser(user);
            like.setSong(song);
            like.setLikedAt(LocalDateTime.now());
            songLikeRepository.save(like);

            song.setLikeCount(song.getLikeCount() + 1);
            songRepository.save(song);

            if (song.isUserUpload() &&
                    song.getUploadedBy() != null &&
                    !song.getUploadedBy().getId().equals(user.getId())) {

                NotificationRequest request = NotificationRequest.builder()
                        .title("Có người thích bài hát của bạn")
                        .message(user.getName() + " vừa thích bài hát " + song.getTitle() + " của bạn.")
                        .type(NotificationType.LIKE)
                        .targetType(NotificationTargetType.SONG)
                        .targetId(song.getId())
                        .build();

                notificationService.createNotification(song.getUploadedBy(), request);
            }

            return true;
        }
    }




    public List<SongResponse> getUserUploadedSongs(User currentUser) {
        List<Song> songs = songRepository.findByIsUserUploadTrueAndUploadedByAndPendingReviewFalseOrderByReleaseDateDesc(currentUser);
        return songs.stream()
                .map(song -> enrichSongResponse(song, currentUser))
                .toList();
    }

    public List<SongResponse> searchUserUploadedSongs(User currentUser, String keyword) {
        List<Song> songs = songRepository.searchUserUploadedSongsByTitleNative(currentUser.getId(), keyword.toLowerCase());
        return songs.stream()
                .map(song -> enrichSongResponse(song, currentUser))
                .toList();
    }

    public List<SongResponse> getRelatedSongs(Long songId, int limit) {
        // 1. Lấy bài hát gốc
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        User currentUser = SecurityUtil.getCurrentUser();

        Set<Song> related = new LinkedHashSet<>();

        if (!song.isUserUpload()) {
            // ✅ Trường hợp bài hát hệ thống
            if (song.getArtists() != null && !song.getArtists().isEmpty()) {
                related.addAll(songRepository.findRelatedSongsByAnyArtist(song.getArtists(), songId));
            }
            related.addAll(songRepository.findRelatedSongsByGenre(
                    song.getGenre(), songId, PageRequest.of(0, limit * 2)));

        } else {
            // ✅ Trường hợp bài hát người dùng đăng
            Long uploaderId = song.getUploadedBy().getId();

            // Bài khác do cùng người dùng upload (trừ bài gốc)
            related.addAll(songRepository.findRelatedUserUploadedSongsByUploader(
                    uploaderId, songId, PageRequest.of(0, limit * 2)));

            // Bài khác cùng thể loại do người khác upload (đang public, không pending)
            related.addAll(songRepository.findRelatedUserUploadedSongsByGenreExceptUploader(
                    song.getGenre().getId(), uploaderId, songId, PageRequest.of(0, limit * 2)));
        }

        // 4. Loại trùng, trộn nhẹ
        List<Song> resultList = new ArrayList<>(related);
        Collections.shuffle(resultList);

        // 5. Cắt giới hạn và map DTO
        return resultList.stream()
                .limit(limit)
                .map(s -> enrichSongResponse(s, currentUser))
                .toList();
    }


    public List<SongResponse> recommendSongs(User user, int limit) {
        List<SongResponse> result = new ArrayList<>();

        // 1. Lấy 1–2 bài vừa nghe gần nhất
        songViewRepository.findRecentlyListenedSongs(user, PageRequest.of(0, 3))
                .forEach(song -> result.add(enrichSongResponse(song, user)));

        // 2. Lấy 1–2 bài được nghe nhiều nhất
        songViewRepository.findMostListenedSongsByUser(user, PageRequest.of(0, 3))
                .forEach(song -> {
                    SongResponse response = enrichSongResponse(song, user);
                    if (!result.contains(response)) result.add(response);
                });

        // 3. Lấy bài hát theo top genres
        List<Long> topGenreIds = songViewRepository.findTopGenreIdsByUser(user);
        int genreLimit = 3;
        topGenreIds = topGenreIds.subList(0, Math.min(genreLimit, topGenreIds.size()));

        if (!topGenreIds.isEmpty()) {
            List<Song> songs = songRepository.findRecommendedByGenres(topGenreIds);
            Collections.shuffle(songs); // shuffle ở bước này
            songs.stream()
                    .map(s -> enrichSongResponse(s, user))
                    .filter(s -> !result.contains(s))
                    .limit(limit - result.size())
                    .forEach(result::add);
        } else {
            // 4. Fallback: bài ngẫu nhiên hệ thống
            songRepository.findRandomSystemSongs(limit - result.size())
                    .stream()
                    .map(s -> enrichSongResponse(s, user))
                    .forEach(result::add);
        }

        // 🎲 Tăng tính ngẫu nhiên: shuffle toàn bộ trước khi limit
        Collections.shuffle(result);
        return result.stream()
                .limit(limit)
                .toList();
    }

    public List<SongResponse> getSongsByAlbum(Long albumId) {
        User currentUser = SecurityUtil.getCurrentUser();
        List<Song> songs = songRepository.findByAlbumIdOrderByReleaseDateDesc(albumId);
        return songs.stream()
                .map(song -> enrichSongResponse(song, currentUser))
                .toList();
    }

    public List<SongResponse> getListeningHistory(User user, boolean onlySystem, int page, int size) {
        int offset = page * size;
        return songViewRepository.findDistinctListeningHistory(user.getId(), onlySystem, size, offset)
                .stream()
                .map(song -> enrichSongResponse(song, user))
                .toList();
    }

    public List<SongResponse> getSongsByArtist(Long artistId, int page, int size) {
        User currentUser = SecurityUtil.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        return songRepository.findByArtistIdAndIsPublicTrueAndIsUserUploadFalseOrderByReleaseDateAsc(artistId, pageable)
                .stream()
                .map(song -> enrichSongResponse(song, currentUser))
                .toList();
    }

    public List<SongResponse> suggestUserUploadedSongs(int limit, int days, int ownSongOffset) {
        LocalDate cutoffDate = LocalDate.now().minusDays(days);
        User currentUser = SecurityUtil.getCurrentUser();

        // 1. Luôn lấy 2 bài của chính user (nếu có)
        Pageable ownPage = PageRequest.of(ownSongOffset, 2);  // offset để phân trang qua các lần gọi
        List<Song> ownSongs = songRepository.findRecentSongsByUser(
                currentUser.getId(),
                LocalDate.now().minusDays(30),
                ownPage
        );

        Set<Song> songSet = new LinkedHashSet<>(ownSongs); // chứa luôn bài của user
        int remaining = limit - songSet.size();

        // 2. Lượt thích
        if (remaining > 0) {
            List<Song> topLiked = songRepository.findTopUserUploadedByLikes(cutoffDate, remaining * 2);
            songSet.addAll(topLiked);
        }

        // 3. Lượt nghe
        if (songSet.size() < limit) {
            List<Song> topListened = songRepository.findTopUserUploadedByListens(cutoffDate, remaining * 2);
            topListened.removeIf(songSet::contains);
            songSet.addAll(topListened);
        }

        // 4. Fallback: bài mới
        if (songSet.size() < limit) {
            List<Song> recent = songRepository.findRecentUserUploadedSongs(limit * 3);
            recent.removeIf(songSet::contains);
            Collections.shuffle(recent);
            for (Song s : recent) {
                songSet.add(s);
                if (songSet.size() >= limit) break;
            }
        }

        // Tách bài của user lên đầu
        List<Song> finalList = new ArrayList<>(ownSongs); // chính chủ trước
        List<Song> others = songSet.stream()
                .filter(s -> !ownSongs.contains(s))
                .collect(Collectors.toList());
        Collections.shuffle(others);

        finalList.addAll(others);
        finalList = finalList.stream().limit(limit).toList();

        return finalList.stream()
                .map(song -> enrichSongResponse(song, currentUser))
                .toList();
    }




}
