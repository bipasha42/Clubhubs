package com.example.clubhub4.service;

import com.example.clubhub4.dto.NotificationView;
import com.example.clubhub4.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Page<NotificationView> list(UUID userId, int page, int size) {
        return notificationRepository.findViewsByRecipientId(
                userId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
    }

    @Transactional
    public void markOneRead(UUID userId, UUID id) {
        notificationRepository.markOneRead(userId, id);
    }

    @Transactional
    public void markAllRead(UUID userId) {
        notificationRepository.markAllRead(userId);
    }
}