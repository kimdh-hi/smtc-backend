package com.sparta.showmethecode.repository.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.showmethecode.domain.ReviewAnswer;
import com.sparta.showmethecode.domain.ReviewRequest;
import com.sparta.showmethecode.domain.ReviewRequestStatus;
import com.sparta.showmethecode.domain.User;
import com.sparta.showmethecode.dto.response.*;
import com.sparta.showmethecode.repository.querydsl.util.OrderByNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.querydsl.core.types.ExpressionUtils.count;
import static com.querydsl.core.types.ExpressionUtils.path;
import static com.sparta.showmethecode.domain.QReviewAnswer.reviewAnswer;
import static com.sparta.showmethecode.domain.QReviewRequest.reviewRequest;
import static com.sparta.showmethecode.domain.QReviewRequestComment.reviewRequestComment;
import static com.sparta.showmethecode.domain.QUser.user;


@Slf4j
public class ReviewRequestDaoImpl extends QuerydslRepositorySupport implements ReviewRequestDao {

    private final EntityManager em;
    private final JPAQueryFactory query;

    public ReviewRequestDaoImpl(JPAQueryFactory jpaQueryFactory, EntityManager entityManager) {
        super(ReviewRequest.class);
        this.em = entityManager;
        this.query = jpaQueryFactory;
    }

    @Override
    public Page<ReviewRequestResponseDto> findReviewRequestList(Pageable pageable, boolean isAsc, ReviewRequestStatus status) {

        JPAQuery<ReviewRequestResponseDto> jpaQuery = query.select(new QReviewRequestResponseDto(
                        reviewRequest.id,
                        reviewRequest.requestUser.username,
                        reviewRequest.requestUser.nickname,
                        reviewRequest.title,
                        reviewRequest.content,
                        reviewRequest.languageName,
                        reviewRequest.status,
                        reviewRequest.createdAt,
                        ExpressionUtils.as(
                                JPAExpressions.select(reviewRequestComment.id.count())
                                        .from(reviewRequestComment)
                                        .where(reviewRequestComment.reviewRequest.eq(reviewRequest)), "commentCount")
                ))
                .where(statusEqual(status))
                .from(reviewRequest);

        JPQLQuery<ReviewRequestResponseDto> pagination = getQuerydsl().applyPagination(pageable, jpaQuery);

        long totalCount = pagination.fetchCount();
        return new PageImpl<>(pagination.fetch(), pageable, totalCount);
    }

    @Override
    public Page<ReviewRequestResponseDto> findSearchByTitleOrCommentAdvanced(String keyword, Pageable pageable, boolean isAsc, ReviewRequestStatus status) {

        List<ReviewRequestResponseDto> results = query
                .select(new QReviewRequestResponseDto(
                        reviewRequest.id,
                        user.username,
                        user.nickname,
                        reviewRequest.title,
                        reviewRequest.content,
                        reviewRequest.languageName,
                        reviewRequest.status,
                        reviewRequest.createdAt,
                        ExpressionUtils.as(JPAExpressions.select(count(reviewRequestComment.id))
                                .from(reviewRequestComment)
                                .where(reviewRequestComment.reviewRequest.eq(reviewRequest)), "commentCount")
                        )
                )
                .from(reviewRequest)
                .join(reviewRequest.requestUser, user)
                .where(containingTitleOrComment(keyword))
                .where(statusEqual(status))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(isAsc ? reviewRequest.createdAt.desc() : reviewRequest.createdAt.asc())
                .fetch();

        JPAQuery<ReviewRequestResponseDto> jpaQuery = query
                .select(new QReviewRequestResponseDto(
                        reviewRequest.id,
                        user.username,
                        user.nickname,
                        reviewRequest.title,
                        reviewRequest.content,
                        reviewRequest.languageName,
                        reviewRequest.status,
                        reviewRequest.createdAt,
                        ExpressionUtils.as(JPAExpressions.select(count(reviewRequestComment.id))
                                .from(reviewRequestComment)
                                .where(reviewRequestComment.reviewRequest.eq(reviewRequest)), "commentCount")
                        )
                )
                .from(reviewRequest)
                .join(reviewRequest.requestUser, user).fetchJoin()
                .where(containingTitleOrComment(keyword));

        return PageableExecutionUtils.getPage(results, pageable, jpaQuery::fetchCount);
    }

    @Override
    public Page<ReviewRequestResponseDto> findSearchByTitleOrComment(String keyword, Pageable pageable) {

        QueryResults<ReviewRequestResponseDto> results
                = query.select(new QReviewRequestResponseDto(
                        reviewRequest.id,
                        user.username,
                        user.nickname,
                        reviewRequest.title,
                        reviewRequest.content,
                        reviewRequest.languageName,
                        reviewRequest.status,
                        reviewRequest.createdAt,
                        ExpressionUtils.as(JPAExpressions.select(count(reviewRequestComment.id))
                        .from(reviewRequestComment)
                        .where(reviewRequestComment.reviewRequest.eq(reviewRequest)), "commentCount")
                 ))
                .from(reviewRequest)
                .join(reviewRequest.requestUser, user).fetchJoin()
                .where(containingTitleOrComment(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<ReviewRequestResponseDto> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public ReviewRequestDetailResponseDto getReviewRequestDetails(Long id) {

        ReviewRequest result = query.select(reviewRequest)
                .from(reviewRequest)
                .join(reviewRequest.requestUser, user).fetchJoin()
                .leftJoin(reviewRequest.reviewAnswer, reviewAnswer).fetchJoin()
                .leftJoin(reviewRequest.reviewRequestComments, reviewRequestComment).fetchJoin()
                .where(reviewRequest.id.eq(id))
                .fetchFirst();

        List<CommentResponseDto> comments = new ArrayList<>();

        if (result.hasComments()) {
            comments = result.getReviewRequestComments().stream().map(
                    c -> new CommentResponseDto(c.getId(), c.getUser().getId(), c.getUser().getUsername(), c.getUser().getNickname(), c.getContent(), c.getCreatedAt())
            ).collect(Collectors.toList());
        }

        for (CommentResponseDto comment : comments) {
            System.out.println(comment);
        }


        ReviewAnswer reviewAnswer = result.getReviewAnswer();
        if (!Objects.isNull(reviewAnswer)) {
            ReviewAnswerResponseDto reviewAnswerResponseDto = new ReviewAnswerResponseDto(
                    reviewAnswer.getId(),
                    result.getId(),
                    reviewAnswer.getAnswerUser().getUsername(),
                    reviewAnswer.getAnswerUser().getNickname(),
                    reviewAnswer.getContent(),
                    reviewAnswer.getPoint(),
                    reviewAnswer.getCreatedAt()
            );
            return new ReviewRequestDetailResponseDto(
                    result.getId(), result.getAnswerUser().getId(),
                    result.getRequestUser().getUsername(), result.getRequestUser().getNickname(),
                    result.getTitle(), result.getContent(),
                    result.getStatus(), result.getLanguageName(), result.getCreatedAt(),
                    comments,
                    reviewAnswerResponseDto
            );
        }
        return new ReviewRequestDetailResponseDto(
                result.getId(), result.getAnswerUser().getId(),
                result.getRequestUser().getUsername(),  result.getRequestUser().getNickname(),
                result.getTitle(), result.getContent(),
                result.getStatus(), result.getLanguageName(), result.getCreatedAt(),
                comments
        );
    }

    @Override
    public List<ReviewRequestLanguageCount> getReviewRequestLanguageCountGroupByLanguage() {
        List<Tuple> result = query.select(reviewRequest.languageName, reviewRequest.id.count())
                .from(reviewRequest)
                .groupBy(reviewRequest.languageName)
                .orderBy(OrderByNull.DEFAULT)
                .fetch();

        return result.stream().map(
                r -> new ReviewRequestLanguageCount(r.get(0, String.class), r.get(1, Long.class))
        ).collect(Collectors.toList());
    }

    @Override
    public Page<ReviewRequestResponseDto> findMyReviewRequestList(Long id, Pageable pageable, ReviewRequestStatus status) {

        JPAQuery<ReviewRequestResponseDto> jpaQuery = query
                .select(new QReviewRequestResponseDto(
                                reviewRequest.id,
                                user.username,
                                user.nickname,
                                reviewRequest.title,
                                reviewRequest.content,
                                reviewRequest.languageName,
                                reviewRequest.status,
                                reviewRequest.createdAt,
                                ExpressionUtils.as(JPAExpressions.select(count(reviewRequestComment.id))
                                        .from(reviewRequestComment)
                                        .where(reviewRequestComment.reviewRequest.eq(reviewRequest)), "commentCount")
                        )
                )
                .from(reviewRequest)
                .join(reviewRequest.requestUser, user)
                .where(user.id.eq(id))
                .where(statusEqual(status))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        JPQLQuery<ReviewRequestResponseDto> pagination = getQuerydsl().applyPagination(pageable, jpaQuery);

        long totalCount = pagination.fetchCount();
        return new PageImpl<>(pagination.fetch(), pageable, totalCount);
    }

    @Override
    public Page<ReviewRequestResponseDto> findMyReceivedRequestList(Long id, Pageable pageable, ReviewRequestStatus status) {
        JPAQuery<ReviewRequestResponseDto> jpaQuery = query
                .select(new QReviewRequestResponseDto(
                                reviewRequest.id,
                                user.username,
                                user.nickname,
                                reviewRequest.title,
                                reviewRequest.content,
                                reviewRequest.languageName,
                                reviewRequest.status,
                                reviewRequest.createdAt,
                                ExpressionUtils.as(JPAExpressions.select(count(reviewRequestComment.id))
                                        .from(reviewRequestComment)
                                        .where(reviewRequestComment.reviewRequest.eq(reviewRequest)), "commentCount")
                        )
                )
                .from(reviewRequest)
                .join(reviewRequest.answerUser, user)
                .where(user.id.eq(id))
                .where(statusEqual(status))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());


        JPQLQuery<ReviewRequestResponseDto> pagination = getQuerydsl().applyPagination(pageable, jpaQuery);
        long totalCount = pagination.fetchCount();

        return new PageImpl<>(pagination.fetch(), pageable, totalCount);
    }

    @Override
    public boolean isMyReviewRequest(Long reviewId, User user) {
        Integer exist = query.selectOne()
                .from(reviewRequest)
                .where(reviewRequest.id.eq(reviewId).and(reviewRequest.requestUser.eq(user)))
                .fetchFirst();

        return exist != null;
    }

    @Override
    public boolean isRequestedToMe(Long reviewId, User reviewer) {
        Integer exist = query.selectOne()
                .from(reviewRequest)
                .where(reviewRequest.id.eq(reviewId).and(reviewRequest.answerUser.eq(reviewer)))
                .fetchFirst();

        return exist != null;
    }

    @Override
    public boolean isAnswerToMe(Long answerId, User user) {
        Integer exist = query.selectOne()
                .from(reviewRequest)
                .where(reviewRequest.reviewAnswer.id.eq(answerId).and(reviewRequest.requestUser.id.eq(user.getId())))
                .fetchFirst();

        return exist != null;
    }

    @Override
    public Page<ReviewRequestResponseDto> searchRequestByLanguageName(String languageName, Pageable pageable, boolean isAsc) {

        JPAQuery<ReviewRequestResponseDto> jpaQuery = query
                .select(new QReviewRequestResponseDto(
                                reviewRequest.id,
                                user.username,
                                user.nickname,
                                reviewRequest.title,
                                reviewRequest.content,
                                reviewRequest.languageName,
                                reviewRequest.status,
                                reviewRequest.createdAt,
                                ExpressionUtils.as(JPAExpressions.select(count(reviewRequestComment.id))
                                        .from(reviewRequestComment)
                                        .where(reviewRequestComment.reviewRequest.eq(reviewRequest)), "commentCount")
                        )
                )
                .from(reviewRequest)
                .join(reviewRequest.requestUser, user)
                .where(reviewRequest.languageName.eq(languageName));

        List<ReviewRequestResponseDto> result = getQuerydsl().applyPagination(pageable, jpaQuery).fetch();

        return PageableExecutionUtils.getPage(result, pageable, jpaQuery::fetchCount);
    }

    @Override
    public Page<ReviewAnswerResponseDto> findMyAnswer(Long userId, Pageable pageable) {
        JPAQuery<ReviewAnswerResponseDto> jpaQuery = query.select(
                        new QReviewAnswerResponseDto(
                                reviewRequest.reviewAnswer.id,
                                reviewRequest.id,
                                reviewRequest.reviewAnswer.answerUser.username,
                                reviewRequest.reviewAnswer.answerUser.nickname,
                                reviewRequest.reviewAnswer.content,
                                reviewRequest.reviewAnswer.point,
                                reviewRequest.reviewAnswer.createdAt
                        )
                ).from(reviewRequest)
                .join(reviewRequest.reviewAnswer, reviewAnswer)
                .where(reviewRequest.reviewAnswer.answerUser.id.eq(userId));

        List<ReviewAnswerResponseDto> result = getQuerydsl().applyPagination(pageable, jpaQuery).fetch();

        return PageableExecutionUtils.getPage(result, pageable, jpaQuery::fetchCount);
    }

    @Override
    public void deleteComment(Long reviewId, Long commentId, Long userId) {
        query.delete(reviewRequest)
                .where(reviewRequest.id.eq(reviewId));
    }

    @Override
    public RequestAndAnswerResponseDto findReviewRequestAndAnswer(Long id) {

        return query.select(
                        new QRequestAndAnswerResponseDto(
                                reviewRequest.id, reviewRequest.requestUser.username,
                                reviewRequest.title, reviewRequest.content, reviewRequest.status, reviewRequest.createdAt,
                                reviewRequest.reviewAnswer.id, reviewRequest.reviewAnswer.content
                        )
                ).from(reviewRequest)
                .join(reviewRequest.requestUser, user)
                .leftJoin(reviewRequest.reviewAnswer, reviewAnswer)
                .where(reviewRequest.id.eq(id))
                .fetchOne();
    }

    private BooleanExpression containingTitleOrComment(String keyword) {
        return Objects.isNull(keyword) || keyword.isEmpty() ? null : reviewRequest.title.contains(keyword).or(reviewRequest.content.contains(keyword));
    }

    private BooleanExpression statusEqual(ReviewRequestStatus status) {
        return !Objects.isNull(status) && !status.equals(ReviewRequestStatus.ALL) ? reviewRequest.status.eq(status) : null;
    }
}
