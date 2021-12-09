package com.sparta.showmethecode.service;

import com.sparta.showmethecode.domain.*;
import com.sparta.showmethecode.dto.request.AddAnswerDto;
import com.sparta.showmethecode.dto.request.UpdateReviewerDto;
import com.sparta.showmethecode.dto.request.EvaluateAnswerDto;
import com.sparta.showmethecode.dto.request.UpdateAnswerDto;
import com.sparta.showmethecode.dto.response.*;
import com.sparta.showmethecode.repository.ReviewAnswerRepository;
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
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewerService {

    private final ReviewAnswerRepository reviewAnswerRepository;
    private final ReviewRequestRepository reviewRequestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    /**
     * 리뷰요청에 대한 답변등록 API
     * 자신에게 요청된 리뷰가 아닌 경우에 대한 처리 필요
     */
    @Transactional
    public void addAnswer(Long reviewerId, Long reviewId, AddAnswerDto addAnswerDto) {
        User reviewer = userRepository.findById(reviewerId).get();
        if (isRequestedToMe(reviewId, reviewer)) {
            ReviewAnswer reviewAnswer = ReviewAnswer.builder()
                    .content(addAnswerDto.getContent())
                    .answerUser(reviewer)
                    .build();
            ReviewAnswer savedReviewAnswer = reviewAnswerRepository.save(reviewAnswer);

            ReviewRequest reviewRequest = reviewRequestRepository.findById(reviewId).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 리뷰요청입니다.")
            );

            reviewer.increaseAnswerCount();
            reviewRequest.setStatus(ReviewRequestStatus.SOLVE);
            reviewRequest.setReviewAnswer(savedReviewAnswer);

            notificationService
                    .send(reviewRequest.getRequestUser(), reviewRequest, "리뷰 등록이 완료되었습니다.", MoveUriType.DETAILS);
        }
    }

    /**
     * 리뷰요청 거절 API
     * 자신에게 요청된 리뷰가 아닌 경우에 대한 처리 필요
     */
    @Transactional
    public void rejectRequestedReview(User reviewer, Long questionId) {
        if (isRequestedToMe(questionId, reviewer)) {
            ReviewRequest reviewRequest = reviewRequestRepository.findById(questionId).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 리뷰요청입니다.")
            );

            // SOLVE(해결됨)이 아닌 경우에만 거절이 가능하도록
            ReviewRequestStatus status = reviewRequest.getStatus();
            if (!status.equals(ReviewRequestStatus.SOLVE) && !status.equals(ReviewRequestStatus.EVALUATED)) {
                reviewRequest.setStatus(ReviewRequestStatus.REJECTED);
                notificationService.send(reviewRequest.getRequestUser(), reviewRequest, "리뷰 요청이 거절되었습니다.", MoveUriType.DETAILS);
            } else {
                throw new IllegalArgumentException("해결되지 않은 리뷰요청에 대해서만 거절이 가능합니다.");
            }
        }
   }

    /**
     * 나에게 요청된 리뷰인지 확인
     */
    private boolean isRequestedToMe(Long questionId, User reviewer) {
        return reviewRequestRepository.isRequestedToMe(questionId, reviewer);
    }

    /**
     * 리뷰어 랭킹 조회 API (전체랭킹 조회)
     */
    @Transactional(readOnly = true)
    public PageResponseDto<ReviewerInfoDto> getReviewerRanking(
            int page, int size, boolean isAsc
    ) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "evalTotal");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> result = userRepository.getReviewerRanking(pageable, isAsc);

        List<ReviewerInfoDto> reviewerInfo = result.getContent().stream().map(
                u -> new ReviewerInfoDto(
                        u.getId(),
                        u.getUsername(),
                        u.getNickname(),
                        u.getLanguages().stream().map(l -> new String(l.getName())).collect(Collectors.toList()),
                        u.getAnswerCount(),
                        u.getEvalTotal() / u.getEvalCount()
                )
        ).collect(Collectors.toList());

        return new PageResponseDto<ReviewerInfoDto>(
                reviewerInfo,
                result.getTotalPages(),
                result.getTotalElements(),
                page, size
        );
    }

    /**
     * 리뷰어 랭킹 조회 API (상위 5명)
     */
    @Transactional(readOnly = true)
    public List<ReviewerInfoDto> getReviewerTop5Ranking(boolean isDesc) {
        return userRepository.getReviewerRankingTop5(isDesc).stream().map(
                u -> new ReviewerInfoDto(
                        u.getId(),
                        u.getUsername(),
                        u.getNickname(),
                        u.getLanguages().stream().map(
                                l -> new String(l.getName())
                        ).collect(Collectors.toList()),
                        u.getAnswerCount(),
                        u.getEvalCount() > 0 ? u.getEvalTotal() / u.getEvalCount() : 0
                )
        ).collect(Collectors.toList());
    }

    /**
     * 내가 답변한 리뷰목록 조회 API
     */
    @Transactional(readOnly = true)
    public PageResponseDto<ReviewAnswerResponseDto> getMyAnswerList(User reviewer, int page, int size, boolean isAsc, String sortBy) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ReviewAnswerResponseDto> myAnswer = reviewRequestRepository.findMyAnswer(reviewer.getId(), pageable);

        return new PageResponseDto<ReviewAnswerResponseDto>(
                myAnswer.getContent(),
                myAnswer.getTotalPages(),
                myAnswer.getTotalElements(),
                page, size
        );
    }

    /**
     * 답변한 리뷰 수정 API
     */
    @Transactional
    public void updateAnswer(User reviewer, Long answerId, UpdateAnswerDto updateAnswerDto) {
        if (isMyAnswer(reviewer.getId(), answerId)) {
            ReviewAnswer reviewAnswer = reviewAnswerRepository.findById(answerId).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 답변입니다.")
            );

            reviewAnswer.update(updateAnswerDto);
        }
    }

    private boolean isMyAnswer(Long reviewerId, Long answerId) {
        return reviewAnswerRepository.isMyAnswer(reviewerId, answerId);
    }

    /**
     * 나에게 요청온 리뷰 조회
     */
    public PageResponseDto getMyReceivedRequestList(User user, int page, int size, String sortBy, boolean isAsc, ReviewRequestStatus status) {
        Pageable pageable = makePageable(page, size, sortBy, isAsc);
        Page<ReviewRequestResponseDto> reviewRequests = reviewRequestRepository.findMyReceivedRequestList(user.getId(), pageable, status);

        return new PageResponseDto<ReviewRequestResponseDto>(
                reviewRequests.getContent(),
                reviewRequests.getTotalPages(),
                reviewRequests.getTotalElements(),
                page, size
        );
    }

    /**
     * 답변에 대한 평가 API
     *
     * 평가하고자 하는 답변이 내가 요청한 코드리뷰에 대한 답변인지 확인해야 함
     */
    @Transactional
    public void evaluateAnswer(User user, Long questionId, Long answerId, EvaluateAnswerDto evaluateAnswerDto) {
        if(reviewRequestRepository.isAnswerToMe(answerId, user)) {
            ReviewAnswer reviewAnswer = reviewAnswerRepository.findById(answerId).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 답변입니다.")
            );

            ReviewRequest reviewRequest = reviewRequestRepository.findById(questionId).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 리뷰요청입니다.")
            );

            reviewRequest.setStatus(ReviewRequestStatus.EVALUATED);

            reviewAnswer.evaluate(evaluateAnswerDto.getPoint());
            reviewAnswer.getAnswerUser().evaluate(evaluateAnswerDto.getPoint());
        }
    }

    /**
     * 리뷰어 변경하기 API
     */
    @Transactional
    public void changeReviewer(UpdateReviewerDto changeReviewerDto, Long questionId, Long reviewerId) {
        ReviewRequest reviewRequest = reviewRequestRepository.findById(questionId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 리뷰요청입니다.")
        );

        Long newReviewerId = changeReviewerDto.getNewReviewerId();
        User newReviewer = userRepository.findById(newReviewerId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 리뷰어입니다.")
        );

        reviewRequest.updateReviewer(newReviewer);

    }

    private Pageable makePageable(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);

        return PageRequest.of(page, size, sort);
    }
}
