package com.sparta.showmethecode.repository.querydsl;

import com.sparta.showmethecode.domain.ReviewRequest;
import com.sparta.showmethecode.domain.ReviewRequestStatus;
import com.sparta.showmethecode.domain.User;
import com.sparta.showmethecode.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewRequestDao {

    // 코드리뷰 목록 조회
    Page<ReviewRequestResponseDto> findReviewRequestList(Pageable pageable, boolean isAsc, ReviewRequestStatus status);

    Page<ReviewRequestResponseDto> findSearchByTitleOrComment(String keyword, Pageable pageable);
    // 코드리뷰요청 목록 제목+내용 검색쿼리
    Page<ReviewRequestResponseDto> findSearchByTitleOrCommentAdvanced(String keyword, Pageable pageable, boolean isAsc, ReviewRequestStatus status);
    // 코드리뷰요청 상세정보 조회
    ReviewRequestDetailResponseDto getReviewRequestDetails(Long id);
    // 언어별 코드리뷰요청 카운팅
    List<ReviewRequestLanguageCount> getReviewRequestLanguageCountGroupByLanguage();
    // 자신이 요청한 리뷰 조회
    Page<ReviewRequestResponseDto> findMyReviewRequestList(Long userId, Pageable pageable, ReviewRequestStatus status);
    // 자신에게 요청된 리뷰 조회
    Page<ReviewRequestResponseDto> findMyReceivedRequestList(Long userId, Pageable pageable, ReviewRequestStatus status);

    // 내가 요청한 리뷰가 맞는지 체크
    boolean isMyReviewRequest(Long reviewId, User user);
    // 나에게 요청된 리뷰가 맞는지 체크
    boolean isRequestedToMe(Long reviewId, User reviewer);
    // 나에게 답변된 리뷰가 맞는지 체크
    boolean isAnswerToMe(Long answerId, User user);

    // 언어이름으로 코드리뷰요청 조회
    Page<ReviewRequestResponseDto> searchRequestByLanguageName(String languageName, Pageable pageable, boolean isAsc);

    // 내가 답변한 리뷰목록 조회
    Page<ReviewAnswerResponseDto> findMyAnswer(Long userId, Pageable pageable);

    // 현재 리뷰요청에 달린 댓글 삭제
    void deleteComment(Long reviewId, Long commentId, Long userId);

    // 리뷰 상세내용 조회 (댓글X)
    RequestAndAnswerResponseDto findReviewRequestAndAnswer(Long id);
}
