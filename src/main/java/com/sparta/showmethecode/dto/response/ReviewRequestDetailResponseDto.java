package com.sparta.showmethecode.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sparta.showmethecode.domain.ReviewRequestStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ReviewRequestDetailResponseDto {

    private Long reviewRequestId;
    private Long answerUserId;
    private String username;
    private String nickname;
    private String title;
    private String content;
    private String languageName;
    private String status;

    private LocalDateTime createdAt;

    private ReviewAnswerResponseDto reviewAnswer;

    private List<CommentResponseDto> comments = new ArrayList<>();


    @QueryProjection
    public ReviewRequestDetailResponseDto(
            Long reviewRequestId, Long answerUserId,
            String username, String nickname,
            String title, String content, ReviewRequestStatus status, String languageName,LocalDateTime createdAt,
            List<CommentResponseDto> comments,
            ReviewAnswerResponseDto reviewAnswer
    ) {
        this.reviewRequestId = reviewRequestId;
        this.answerUserId = answerUserId;
        this.username = username;
        this.nickname = nickname;
        this.title = title;
        this.content = content;
        this.status = status.getDescription();
        this.languageName = languageName;
        this.createdAt = createdAt;
        this.comments = comments;
        this.reviewAnswer = reviewAnswer;
    }

    public ReviewRequestDetailResponseDto(
            Long reviewRequestId, Long answerUserId,
            String username, String nickname,
            String title, String content, ReviewRequestStatus status, String languageName, LocalDateTime createdAt,
            List<CommentResponseDto> comments
    ) {
        this.reviewRequestId = reviewRequestId;
        this.answerUserId = answerUserId;
        this.username = username;
        this.nickname = nickname;
        this.title = title;
        this.content = content;
        this.languageName = languageName;
        this.status = status.getDescription();
        this.createdAt = createdAt;
        this.comments = comments;
    }
}
