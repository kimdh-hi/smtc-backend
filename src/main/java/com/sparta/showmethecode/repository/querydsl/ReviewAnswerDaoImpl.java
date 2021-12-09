package com.sparta.showmethecode.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.sparta.showmethecode.domain.QReviewAnswer.reviewAnswer;

@RequiredArgsConstructor
public class ReviewAnswerDaoImpl implements ReviewAnswerDao{

    private final JPAQueryFactory query;

    @Override
    public boolean isEvaluated(Long answerId) {
        Integer exist = query.selectOne()
                .from(reviewAnswer)
                .where(reviewAnswer.point.gt(0))
                .fetchFirst();

        return exist != null;
    }

    @Override
    public boolean isMyAnswer(Long reviewerId, Long answerId) {

        Integer exist = query.selectOne()
                .from(reviewAnswer)
                .where(reviewAnswer.answerUser.id.eq(reviewerId).
                        and(reviewAnswer.id.eq(answerId))
                )
                .fetchFirst();

        return exist != null;
    }
}
