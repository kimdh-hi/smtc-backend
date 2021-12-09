package com.sparta.showmethecode.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.showmethecode.domain.QReviewRequestComment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.sparta.showmethecode.domain.QReviewRequestComment.reviewRequestComment;


@Slf4j
@RequiredArgsConstructor
public class ReviewRequestCommentDaoImpl implements ReviewRequestCommentDao {

    private final JPAQueryFactory query;


    @Override
    public long deleteComment(Long userId, Long commentId) {
        return query.delete(reviewRequestComment)
                .where(reviewRequestComment.user.id.eq(userId)
                        .and(reviewRequestComment.id.eq(commentId)))
                .execute();
    }

    @Override
    public boolean isMyComment(Long commentId, Long userId) {

        Integer exist = query.selectOne()
                .from(reviewRequestComment)
                .where(reviewRequestComment.id.eq(commentId)
                        .and(reviewRequestComment.user.id.eq(userId)))
                .fetchFirst();

        return exist != null;
    }
}
