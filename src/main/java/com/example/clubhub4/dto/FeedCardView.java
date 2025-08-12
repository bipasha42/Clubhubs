package com.example.clubhub4.dto;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
public class FeedCardView {
    private final UUID postId;
    private final UUID clubId;
    private final String clubName;
    private final String authorFullName;
    private final String content;
    private final String preview;
    private final OffsetDateTime createdAt;
    private final long likeCount;
    private final long commentCount;

    public FeedCardView(UUID postId,
                        UUID clubId,
                        String clubName,
                        String authorFullName,
                        String content,
                        OffsetDateTime createdAt,
                        long likeCount,
                        long commentCount) {
        this.postId = postId;
        this.clubId = clubId;
        this.clubName = clubName;
        this.authorFullName = authorFullName;
        this.content = content;
        this.preview = firstLine(content, 160);
        this.createdAt = createdAt;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

    private static String firstLine(String text, int maxLen) {
        if (text == null) return "";
        int nl = text.indexOf('\n');
        String line = nl >= 0 ? text.substring(0, nl) : text;
        return line.length() > maxLen ? line.substring(0, maxLen) + "…" : line;
    }

}