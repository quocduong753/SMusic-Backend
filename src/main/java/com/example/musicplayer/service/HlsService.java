package com.example.musicplayer.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class HlsService {

    private final Path mp3Dir = Paths.get("mp3");        // Thư mục chứa file gốc
    private final Path hlsBaseDir = Paths.get("hls");     // Thư mục chứa thư mục HLS

    public Path convertToHls(String title, MultipartFile inputFile) throws IOException, InterruptedException {
        String slug = slugify(title);

        // Tạo thư mục nếu chưa có
        Files.createDirectories(mp3Dir);
        Files.createDirectories(hlsBaseDir);

        // 1. Lưu file nhạc gốc vào mp3/
        String extension = getFileExtension(inputFile.getOriginalFilename());
        Path mp3Path = mp3Dir.resolve(slug + "." + extension);

        try (InputStream in = inputFile.getInputStream()) {
            Files.copy(in, mp3Path, StandardCopyOption.REPLACE_EXISTING);
        }

        System.out.println("📁 Đã lưu file gốc tại: " + mp3Path.toAbsolutePath());
        System.out.println("📦 Tồn tại? " + Files.exists(mp3Path) + " | Kích thước: " + Files.size(mp3Path) + " bytes");

        // 2. Tạo thư mục HLS riêng
        Path songHlsDir = hlsBaseDir.resolve(slug);
        deleteDirectoryIfExists(songHlsDir);
        Files.createDirectories(songHlsDir);

        // 3. In thông tin file gốc
        logOriginalAudioInfo(mp3Path);

        // 4. Chuyển đổi
        convertSingleQuality(songHlsDir, mp3Path, "low", "64k");
        convertSingleQuality(songHlsDir, mp3Path, "high", "320k");

        // 5. Tạo master.m3u8
        createMasterPlaylist(songHlsDir);

        System.out.println("✅ HLS sinh tại: " + songHlsDir.toAbsolutePath());
        return songHlsDir;
    }

    // --------------------- HLS HELPER -----------------------

    private void convertSingleQuality(Path dir, Path inputFile, String label, String bitrate)
            throws IOException, InterruptedException {
        String output = dir.resolve(label + ".m3u8").toString();
        String segmentPattern = dir.resolve(label + "_%03d.ts").toString();

        ProcessBuilder builder = new ProcessBuilder("ffmpeg",
                "-i", inputFile.toString(),
                "-map", "0:a",
                "-c:a", "aac",
                "-b:a", bitrate,
                "-hls_time", "5",
                "-hls_playlist_type", "vod",
                "-hls_segment_filename", segmentPattern,
                output
        );
        runProcess(builder);
    }

    private void createMasterPlaylist(Path dir) throws IOException {
        List<String> lines = List.of(
                "#EXTM3U",
                "#EXT-X-STREAM-INF:BANDWIDTH=64000,NAME=\"Low\"",
                "low.m3u8",
                "#EXT-X-STREAM-INF:BANDWIDTH=320000,NAME=\"High\"",
                "high.m3u8"
        );
        Files.write(dir.resolve("master.m3u8"), lines);
    }

    private void logOriginalAudioInfo(Path inputFile) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("ffprobe",
                "-v", "error",
                "-select_streams", "a:0",
                "-show_entries", "stream=bit_rate,sample_rate",
                "-of", "default=noprint_wrappers=1:nokey=0",
                inputFile.toString());
        runProcess(builder);
    }

    private void runProcess(ProcessBuilder builder) throws IOException, InterruptedException {
        builder.redirectErrorStream(true);
        Process process = builder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            reader.lines().forEach(System.out::println);
        }
        int exitCode = process.waitFor();
        System.out.println("⏹ Exit code: " + exitCode);
        if (exitCode != 0) {
            throw new RuntimeException("Lỗi khi chạy ffmpeg/ffprobe (exitCode=" + exitCode + ")");
        }
    }

    // ------------------ CLEAN UP ----------------------

    public void deleteDirectoryIfExists(Path path) {
        if (!Files.exists(path)) return;
        try {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteMp3IfExists(String title) {
        String slug = slugify(title);
        try (Stream<Path> files = Files.list(mp3Dir)) {
            files.filter(f -> f.getFileName().toString().startsWith(slug + "."))
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                            System.out.println("🗑️ Đã xoá file gốc: " + p.getFileName());
                        } catch (IOException e) {
                            System.err.println("❌ Không thể xoá file gốc: " + p);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ------------------ UTIL ----------------------

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) return "mp3";
        return filename.substring(lastDot + 1).toLowerCase();
    }

    private String slugify(String input) {
        return input.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("^-+|-+$", "");
    }
}
