package com.sparta.showmethecode.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sparta.showmethecode.domain.ReviewRequestStatus;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Data
public class ReviewRequestResponseDto {

    private Long reviewRequestId;
    private String username;
    private String nickname;
    private String title;
    private String content;

    private String languageName;
    private String status;

    private LocalDateTime createdAt;

    private long commentCount;

    @QueryProjection
    public ReviewRequestResponseDto(Long reviewRequestId, String username, String nickname, String title, String content, String languageName, ReviewRequestStatus status, LocalDateTime createdAt, long commentCount) {
        this.reviewRequestId = reviewRequestId;
        this.username = username;
        this.nickname = nickname;
        this.title = title;
        this.content = content;
        this.languageName = languageName;
        this.status = status.getDescription();
        this.createdAt = createdAt;
        this.commentCount = commentCount;
    }
}
