package com.sparta.showmethecode.controller;

import com.sparta.showmethecode.domain.ReviewRequestStatus;
import com.sparta.showmethecode.domain.User;
import com.sparta.showmethecode.dto.request.ReviewRequestDto;
import com.sparta.showmethecode.dto.request.ReviewRequestUpdateDto;
import com.sparta.showmethecode.dto.response.*;
import com.sparta.showmethecode.security.UserDetailsImpl;
import com.sparta.showmethecode.service.ReviewRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ReviewRequestController {

    private final ReviewRequestService reviewRequestService;

    /**
     * 코드리뷰 요청목록 API
     */
    @GetMapping("/questions")
    public ResponseEntity<PageResponseDto> getReviewRequestList(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "true") Boolean isAsc,
            @RequestParam(required = false) String query, @RequestParam(required = false, defaultValue = "ALL") ReviewRequestStatus status
    ) {
        --page;

        if (!Objects.isNull(query)) {
            return ResponseEntity.ok(reviewRequestService.searchByTitleOrComment(query, page, size, sortBy, isAsc, status));
        }

        PageResponseDto result = reviewRequestService.getReviewRequestList(page, size, sortBy, isAsc, status);

        return ResponseEntity.ok(result);
    }

    /**
     * 코드리뷰 요청 API
     * SSE 이벤트 포함
     */
    @Secured({"ROLE_USER", "ROLE_REVIEWER"})
    @PostMapping("/question")
    public ResponseEntity<String> addReviewRequest(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ReviewRequestDto requestDto) {

        User user = userDetails.getUser();
        reviewRequestService.addReviewRequest(requestDto, user);

        return ResponseEntity.ok("ok");
    }

    /**
     * 코드리뷰 단건조회 API (코드리뷰 요청 상세정보)
     */
    @GetMapping("/question")
    public ResponseEntity<ReviewRequestDetailResponseDto> getReviewRequest(@RequestParam Long id) {
        ReviewRequestDetailResponseDto reviewRequest = reviewRequestService.getReviewRequest(id);

        return ResponseEntity.ok(reviewRequest);
    }

    /**
     * 코드리뷰 수정 API
     */
    @Secured({"ROLE_USER", "ROLE_REVIEWER"})
    @PutMapping("/question/{questionId}")
    public ResponseEntity updateReviewRequest(
            @RequestBody ReviewRequestUpdateDto updateDto,
            @PathVariable Long questionId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User user = userDetails.getUser();
        reviewRequestService.updateReviewRequest(updateDto, questionId, user);

        return ResponseEntity.ok("ok");
    }

    /**
     * 코드리뷰 삭제 API
     */
    @Secured({"ROLE_USER", "ROLE_REVIEWER"})
    @DeleteMapping("/question/{id}")
    public ResponseEntity deleteReviewRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        reviewRequestService.deleteReviewRequest(id, user);
        return ResponseEntity.ok("ok");
    }

    /**
     * 코드리뷰 요청 언어별 카운팅 API
     */
    @GetMapping("/question/languages/count")
    public List<ReviewRequestLanguageCount> getCountGroupByLanguageName() {
        return reviewRequestService.getCountGroupByLanguageName();
    }

    /**
     * 코드리뷰 요청 언어이름 검색 API
     */
    @GetMapping("/question/language")
    public ResponseEntity searchRequestByLanguageName(
            @RequestParam String language,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "true") boolean isAsc
    ) {
        --page;
        PageResponseDto<ReviewRequestResponseDto> result = reviewRequestService.searchRequestByLanguageName(language, page, size, isAsc);

        return ResponseEntity.ok(result);
    }

    /**
     * 답변을 위한 상세정보 조회 (댓글 X, 답변 O)
     */
    @GetMapping("/details/{id}/answer")
    public ResponseEntity getReviewRequestDetailsWithoutComments(
            @PathVariable Long id
    ) {
        RequestAndAnswerResponseDto response = reviewRequestService.getReviewRequestWithAnswer(id);

        return ResponseEntity.ok(response);
    }
}
