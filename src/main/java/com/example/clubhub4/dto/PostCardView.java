package com.example.clubhub4.dto;

import java.time.OffsetDateTime;
import java.util.UUID;


public class PostCardView {
    private final UUID id;
    private final String content;
    private final String imageUrl;          // <-- add
    private final String preview;
    private final OffsetDateTime createdAt;
    private final long likeCount;
    private final long commentCount;

    public PostCardView(UUID id, String content, String imageUrl, OffsetDateTime createdAt, long likeCount, long commentCount) {
        this.id = id;
        this.content = content;
        this.imageUrl = imageUrl;
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

    public UUID getId() { return id; }
    public String getContent() { return content; }
    public String getImageUrl() { return imageUrl; }   // <-- add getter
    public String getPreview() { return preview; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public long getLikeCount() { return likeCount; }
    public long getCommentCount() { return commentCount; }
}