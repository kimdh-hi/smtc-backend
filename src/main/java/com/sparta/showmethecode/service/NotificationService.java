package com.sparta.showmethecode.service;

import com.sparta.showmethecode.domain.MoveUriType;
import com.sparta.showmethecode.domain.Notification;
import com.sparta.showmethecode.domain.ReviewRequest;
import com.sparta.showmethecode.domain.User;
import com.sparta.showmethecode.dto.response.NotificationResponse;
import com.sparta.showmethecode.dto.response.NotificationsResponse;
import com.sparta.showmethecode.repository.EmitterRepository;
import com.sparta.showmethecode.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@Service
public class NotificationService {


    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    public final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    public NotificationService(EmitterRepository emitterRepository, NotificationRepository notificationRepository){
        this.emitterRepository = emitterRepository;
        this.notificationRepository = notificationRepository;
    }

    public SseEmitter subscribe(Long userId, String lastEventId){

        String id = userId + "_" + System.currentTimeMillis();
        SseEmitter emitter = emitterRepository.save(id, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(id));
        emitter.onTimeout(() -> emitterRepository.deleteById(id));

        // 503 에러를 방지하기 위한 더미 이벤트 전송
        sendToClient(emitter, id, "EventStream Created. [userid =" + userId + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithId(String.valueOf(userId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }

        return emitter;

    }

    private void sendToClient(SseEmitter emitter, String id, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name("sse")
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(id);
            log.error("SSE 연결 오류!", exception);
        }
    }

    @Transactional
    public void send(User receiver, ReviewRequest review, String content, MoveUriType type){
        Notification notification = createNotification(receiver, review, content, type);
        String id = String.valueOf(receiver.getId());

        log.info("Notification send id = {}, type = {}", id, type.toString());

        notificationRepository.save(notification);

        log.info("Notification id = {}", id);

        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllStarWithById(id);
        sseEmitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    sendToClient(emitter, key, NotificationResponse.from(notification));
                }
        );

    }

    private Notification createNotification(User receiver, ReviewRequest review, String content, MoveUriType type){
        if (type.equals(MoveUriType.DETAILS)) {
            return Notification.builder()
                    .receiver(receiver)
                    .content(content)
                    .review(review)
                    .url("/details.html?id=" + review.getId())
                    .isRead(false)
                    .build();
        } else {
            return Notification.builder()
                    .receiver(receiver)
                    .content(content)
                    .review(review)
                    .url("/answer.html?id=" + review.getId())
                    .isRead(false)
                    .build();
        }
    }




    @Transactional
    public NotificationsResponse findAllById(Long userId) {
        List<NotificationResponse> responses = notificationRepository.findAllByReceiverId(userId).stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
        long unreadCount = responses.stream()
                .filter(notification -> !notification.isRead())
                .count();
        return NotificationsResponse.of(responses, unreadCount);
    }

    @Transactional
    public void readNotification(Long id){
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 알림입니다."));
        notification.read();
    }




}






