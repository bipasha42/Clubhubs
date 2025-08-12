package com.example.clubhub4.service;

import com.example.clubhub4.dto.FeedCardView;
import com.example.clubhub4.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentFeedService {
    private final PostRepository postRepository;

    public Page<FeedCardView> getFeed(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findFeedCardsForUser(userId, pageable);
    }
}