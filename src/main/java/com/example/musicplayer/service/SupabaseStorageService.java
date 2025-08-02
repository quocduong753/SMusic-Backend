package com.example.musicplayer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class SupabaseStorageService {

    private static final String SUPABASE_URL = "https://iulveldodqgetnxfnlrw.supabase.co";
    private static final String SUPABASE_BUCKET = "music";
    private static final String SUPABASE_KEY = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Iml1bHZlbGRvZHFnZXRueGZubHJ3Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0OTM5MzEyOCwiZXhwIjoyMDY0OTY5MTI4fQ.Dwwh4CTMz8Ub6_RqjFND9ZtzgaqKg3WkL3TIG6kCedI"; // Service Role Key

    private static final String COVER_DIR = "cover";
    private static final String HLS_DIR = "song_user";

    private final RestTemplate restTemplate = new RestTemplate();

    // ================= UPLOAD COVER IMAGE =================
    public String uploadCoverImage(File imageFile, String title) {
        String filename = generateFilename(title, "jpg");
        String path = COVER_DIR + "/" + filename;

        uploadFile(imageFile, path);

        return buildPublicUrl(path);
    }

    public void deleteCoverImage(String filename) {
        deleteFile(COVER_DIR + "/" + filename);
    }

    // ================= UPLOAD HLS FOLDER =================
    public String uploadHlsFolder(Path folder, String folderName) throws IOException {
        Files.walk(folder)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    String path = HLS_DIR + "/" + folderName + "/" + file.getFileName();
                    uploadFile(file.toFile(), path);
                });
        uploadOriginalMp3(folderName);
        // Trả về đường dẫn public tới master.m3u8
        return buildPublicUrl(HLS_DIR + "/" + folderName + "/master.m3u8");
    }

    // ================== HELPER METHODS ==================

    private void uploadFile(File file, String path) {
        String url = SUPABASE_URL + "/storage/v1/object/" + SUPABASE_BUCKET + "/" + path;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", SUPABASE_KEY);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("❌ Upload thất bại: " + response.getStatusCode() + " - " + response.getBody());
        }

        System.out.println("✅ Đã upload: " + path);
    }

    private void deleteFile(String path) {
        String url = SUPABASE_URL + "/storage/v1/object/" + SUPABASE_BUCKET + "/" + path;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", SUPABASE_KEY);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("❌ Xoá file thất bại: " + response.getStatusCode() + " - " + response.getBody());
        }

        System.out.println("🗑️ Đã xoá file: " + path);
    }

    private void uploadOriginalMp3(String folderName) {
        try {
            Path mp3Path = Files.list(Paths.get("mp3"))
                    .filter(path -> path.getFileName().toString().startsWith(folderName + "."))
                    .findFirst()
                    .orElse(null);

            if (mp3Path != null) {
                String uploadPath = "song_u_mp3/" + folderName + ".mp3";
                uploadFile(mp3Path.toFile(), uploadPath);
                System.out.println("🎵 Đã upload mp3: " + uploadPath);
            } else {
                System.out.println("⚠️ Không tìm thấy mp3 để upload cho: " + folderName);
            }
        } catch (IOException e) {
            System.err.println("❌ Lỗi khi upload mp3: " + e.getMessage());
        }
    }


    private String buildPublicUrl(String path) {
        return SUPABASE_URL + "/storage/v1/object/public/" + SUPABASE_BUCKET + "/" + path;
    }

    private String generateFilename(String base, String extension) {
        return slugify(base) + "-" + System.currentTimeMillis() + "." + extension;
    }

    private String slugify(String input) {
        return input.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("^-+|-+$", "");
    }
}
