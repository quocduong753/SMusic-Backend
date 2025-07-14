package com.example.musicplayer.model;

import com.example.musicplayer.enums.ReportStatus;
import com.example.musicplayer.enums.ViolationReason;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SongReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Song song;

    @ManyToOne
    private User reporter; // null nếu là AI

    @Enumerated(EnumType.STRING)
    private ViolationReason reason;

    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.PENDING;

    private String description; // mô tả chi tiết (tuỳ chọn)

    private boolean isByAI = false;

    private LocalDateTime reportedAt = LocalDateTime.now();
}
