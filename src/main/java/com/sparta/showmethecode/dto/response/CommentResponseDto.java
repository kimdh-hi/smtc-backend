package com.sparta.showmethecode.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponseDto {

    private Long commentId;
    private Long userId;
    private String username;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;

    @QueryProjection
    public CommentResponseDto(Long commentId, Long userId, String username, String nickname, String content, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.content = content;
        this.createdAt = createdAt;
    }
}
