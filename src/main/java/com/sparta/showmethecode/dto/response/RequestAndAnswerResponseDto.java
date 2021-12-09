package com.sparta.showmethecode.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sparta.showmethecode.domain.ReviewRequestStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestAndAnswerResponseDto {

    private Long questionId;
    private String username;
    private String nickname;
    private String title;
    private String content;
    private String status;
    private LocalDateTime createdAt;

    private Long answerId;
    private String answerContent;

    @QueryProjection
    public RequestAndAnswerResponseDto(Long questionId, String username, String title, String content, ReviewRequestStatus status, LocalDateTime createdAt, Long answerId, String answerContent) {
        this.questionId = questionId;
        this.username = username;
        this.title = title;
        this.content = content;
        this.status = status.getDescription();
        this.createdAt = createdAt;
        this.answerId = answerId;
        this.answerContent = answerContent;
    }
}
