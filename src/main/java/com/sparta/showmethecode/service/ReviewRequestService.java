package com.sparta.showmethecode.service;

import com.sparta.showmethecode.domain.MoveUriType;
import com.sparta.showmethecode.domain.ReviewRequest;
import com.sparta.showmethecode.domain.ReviewRequestStatus;
import com.sparta.showmethecode.domain.User;
import com.sparta.showmethecode.dto.request.ReviewRequestDto;
import com.sparta.showmethecode.dto.request.ReviewRequestUpdateDto;
import com.sparta.showmethecode.dto.response.*;
import com.sparta.showmethecode.repository.ReviewRequestCommentRepository;
import com.sparta.showmethecode.repository.ReviewRequestRepository;
import com.sparta.showmethecode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ReviewRequestService {

    private final ReviewRequestRepository reviewRequestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ReviewRequestCommentRepository reviewRequestCommentRepository;

    /**
     * 코드리뷰 요청목록 API
     */
    @Transactional(readOnly = true)
    public PageResponseDto getReviewRequestList(int page, int size, String sortBy, boolean isAsc, ReviewRequestStatus status) {
        Pageable pageable = makePageable(page, size, sortBy, isAsc);

        Page<ReviewRequestResponseDto> reviewRequestList = reviewRequestRepository.findReviewRequestList(pageable, isAsc, status);

        return new PageResponseDto<ReviewRequestResponseDto>(
                reviewRequestList.getContent(),
                reviewRequestList.getTotalPages(),
                reviewRequestList.getTotalElements(),
                page, size
        );
    }

    /**
     * 코드리뷰 검색 API
     */
    @Transactional(readOnly = true)
    public PageResponseDto<ReviewRequestResponseDto> searchByTitleOrComment(
            String keyword,
            int page, int size, String sortBy, boolean isAsc,
            ReviewRequestStatus status
    ) {
        Pageable pageable = makePageable(page, size, sortBy, isAsc);
        Page<ReviewRequestResponseDto> results = reviewRequestRepository.findSearchByTitleOrCommentAdvanced(keyword, pageable, isAsc, status);

        return new PageResponseDto<ReviewRequestResponseDto>(
                results.getContent(),
                results.getTotalPages(), results.getTotalElements(), page, size
        );
    }

    /**
     * 코드리뷰 요청 API
     * SSE 이벤트 포함
     */
    @Transactional
    public void addReviewRequest(ReviewRequestDto requestDto, User user) {
        User reviewer = userRepository.findById(requestDto.getReviewerId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 리뷰어입니다.")
        );

        ReviewRequest reviewRequest
                = new ReviewRequest(user, reviewer, requestDto.getTitle(), requestDto.getContent(), ReviewRequestStatus.UNSOLVE, requestDto.getLanguage().toUpperCase());

        reviewRequestRepository.save(reviewRequest);

        notificationService
                .send(reviewRequest.getAnswerUser(), reviewRequest, "새로운 리뷰 요청이 도착했습니다!", MoveUriType.ANSWER);
    }

    /**
     * 코드리뷰 수정 API
     */
    @Transactional
    public void updateReviewRequest(ReviewRequestUpdateDto updateDto, Long reviewId, User user) {
        boolean isMyRequest = reviewRequestRepository.isMyReviewRequest(reviewId, user);
        if (isMyRequest) {
            ReviewRequest reviewRequest = reviewRequestRepository.findById(reviewId).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 요청입니다.")
            );

            reviewRequest.update(updateDto);
        }
    }

    /**
     * 코드리뷰 삭제 API
     */
    @Transactional
    public void deleteReviewRequest(Long reviewId, User user){
        boolean isMyRequest = reviewRequestRepository.isMyReviewRequest(reviewId, user);
        if (isMyRequest) {
            reviewRequestRepository.deleteById(reviewId);
        }
    }

    /**
     * 코드리뷰 단건조회 API (코드리뷰 요청 상세정보)
     */
    @Transactional(readOnly = true)
    public ReviewRequestDetailResponseDto getReviewRequest(Long id) {
        ReviewRequestDetailResponseDto result = reviewRequestRepository.getReviewRequestDetails(id);
        return result;
    }

    /**
     * 코드리뷰 요청 언어별 카운팅 API
     */
    @Transactional(readOnly = true)
    public List<ReviewRequestLanguageCount> getCountGroupByLanguageName() {
        return reviewRequestRepository.getReviewRequestLanguageCountGroupByLanguage();
    }

    /**
     * 코드리뷰 요청 언어이름 검색 API
     */
    public PageResponseDto<ReviewRequestResponseDto> searchRequestByLanguageName(String language, int page, int size, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        language = language.toUpperCase();

        Page<ReviewRequestResponseDto> reviewRequests = reviewRequestRepository.searchRequestByLanguageName(language, pageable, isAsc);

        return new PageResponseDto<ReviewRequestResponseDto>(
                reviewRequests.getContent(),
                reviewRequests.getTotalPages(),
                reviewRequests.getTotalElements(),
                page, size
        );
    }

    /**
     * 답변을 위한 상세정보 조회 (댓글 X, 답변 O)
     */
    public RequestAndAnswerResponseDto getReviewRequestWithAnswer(Long id) {
        RequestAndAnswerResponseDto reviewRequestAndAnswer = reviewRequestRepository.findReviewRequestAndAnswer(id);
        return reviewRequestAndAnswer;
    }

    private Pageable makePageable(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);

        return PageRequest.of(page, size, sort);
    }


}
