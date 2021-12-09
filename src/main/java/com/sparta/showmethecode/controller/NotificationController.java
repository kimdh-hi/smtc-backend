package com.sparta.showmethecode.controller;

import com.sparta.showmethecode.dto.response.NotificationsResponse;
import com.sparta.showmethecode.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
public class NotificationController {


    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService){
        this.notificationService = notificationService;
    }

    /**
     * 유저 sse 연결
     */
    @CrossOrigin
    @Secured({"ROLE_USER", "ROLE_REVIEWER"})
    @GetMapping(value = "/subscribe/{id}", consumes = MediaType.ALL_VALUE)
    public SseEmitter subscribe(@PathVariable Long id,
                                @RequestParam(value = "lastEventId", required = false, defaultValue = "") String lastEventId) {

        log.info("SSE 연결 id = {}", id);

        return notificationService.subscribe(id, lastEventId);
    }

    /**
     *  로그인 한 유저의 모든 알림 조회
     */
    @Secured({"ROLE_USER", "ROLE_REVIEWER"})
    @GetMapping("/notifications")
    public ResponseEntity<NotificationsResponse> notifications(@PathVariable Long id) {
        return ResponseEntity.ok().body(notificationService.findAllById(id));
    }

    /**
     *  알림 읽음 상태 변경
     */
    @Secured({"ROLE_USER", "ROLE_REVIEWER"})
    @PatchMapping("/notifications/{id}")
    public ResponseEntity<Void> readNotification(@PathVariable Long id) {
        notificationService.readNotification(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
