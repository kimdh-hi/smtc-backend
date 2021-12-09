package com.sparta.showmethecode.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Builder
@Data
public class ReviewAnswerResponseDto {

    private Long reviewAnswerId;
    private Long reviewRequestId;

    private String username;
    private String nickname;

    private String answerContent;

    private double point;

    private LocalDateTime createdAt;

    @QueryProjection
    public ReviewAnswerResponseDto(
            Long reviewAnswerId, Long reviewRequestId, String username, String nickname, String answerContent, double point, LocalDateTime createdAt
    ) {
        this.reviewAnswerId = reviewAnswerId;
        this.reviewRequestId = reviewRequestId;
        this.username = username;
        this.nickname = nickname;
        this.answerContent = answerContent;
        this.point = point;
        this.createdAt = createdAt;
    }
}
