package com.sparta.showmethecode.dto.response;

import com.sparta.showmethecode.domain.Notification;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationResponse {
    private Long id;
    private String content;
    private String url;
//    private Integer[] creatdAt;
    private boolean read;

    @Builder
    public NotificationResponse(Long id, String content, String url, boolean read){
        this.id = id;
        this.content = content;
        this.url = url;
//        this.creatdAt = LocalDateTime.convert(creatdAt);
        this.read = read;
    }

    public static NotificationResponse from(Notification notification){
        return NotificationResponse.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .url(notification.getUrl())
//                .creatdAt(notification.getCreatedAt())
                .read(notification.isRead())
                .build();
    }

}
