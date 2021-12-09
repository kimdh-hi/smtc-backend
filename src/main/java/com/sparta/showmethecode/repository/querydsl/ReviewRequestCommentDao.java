package com.sparta.showmethecode.repository.querydsl;

public interface ReviewRequestCommentDao {

    // 댓글삭제
    long deleteComment(Long commentOwnerId, Long commentId);

    // 내가 작성한 댓글이 맞는지 확인
    boolean isMyComment(Long commentId, Long userId);
}
