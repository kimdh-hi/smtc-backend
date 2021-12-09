package com.sparta.showmethecode.controller;

import com.sparta.showmethecode.domain.User;
import com.sparta.showmethecode.dto.request.AddCommentDto;
import com.sparta.showmethecode.dto.request.UpdateCommentDto;
import com.sparta.showmethecode.dto.request.UpdateReviewDto;
import com.sparta.showmethecode.security.UserDetailsImpl;
import com.sparta.showmethecode.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
public class CommentController {

    private final CommentService commentService;

    /**
     * 리뷰요청 - 댓글추가 API
     */
    @Secured({"ROLE_USER", "ROLE_REVIEWER"})
    @PostMapping("/question/{questionId}/comment")
    public ResponseEntity addComment_Question(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long questionId,
            @RequestBody AddCommentDto addCommentDto
    ) {
        User user = userDetails.getUser();
        commentService.addComment_Question(user, questionId, addCommentDto);

        return ResponseEntity.ok().body("댓글작성 완료");
    }

    /**
     * 리뷰요청 - 댓글삭제 API
     */
    @Secured({"ROLE_USER", "ROLE_REVIEWER"})
    @DeleteMapping("/question/comment/{commentId}")
    public ResponseEntity removeComment_Question(
            @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long commentId
    ) {
        User user = userDetails.getUser();
        long row = commentService.removeComment_Question(user, commentId);

        return ResponseEntity.ok().body("댓글삭제 완료");
    }

    /**
     * 리뷰요청 - 댓글수정 API
     */
    @Secured({"ROLE_USER", "ROLE_REVIEWER"})
    @PutMapping("/question/comment/{commentId}")
    public ResponseEntity updateComment_Question(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long commentId,
            @RequestBody UpdateCommentDto updateCommentDto
    ) {
        User user = userDetails.getUser();
        commentService.updateComment_Question(user, commentId, updateCommentDto);

        return ResponseEntity.ok().body("댓글수정 완료");
    }

    /**
     * 리뷰답변 - 댓글추가 API
     */
    @Secured({"ROLE_USER", "ROLE_REVIEWER"})
    @PostMapping("/answer/{answerId}/comment")
    public ResponseEntity addComment_Answer(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long answerId,
            @RequestBody AddCommentDto addCommentDto
    ) {
        User user = userDetails.getUser();
        commentService.addComment_Answer(user, answerId, addCommentDto);

        return ResponseEntity.ok().body("댓글작성 완료");
    }

    /**
     * 리뷰답변 - 댓글수정 API
     */
    @Secured({"ROLE_USER", "ROLE_REVIEWER"})
    @PutMapping("/answer/comment/{commentId}")
    public ResponseEntity updateComment_Answer(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long commentId,
            @RequestBody UpdateCommentDto updateCommentDto
    ) {
        User user = userDetails.getUser();
        commentService.updateComment_Answer(user, commentId, updateCommentDto);

        return ResponseEntity.ok().body("댓글작성 완료");
    }

    /**
     * 리뷰답변 - 댓글삭제 API
     */
    @Secured({"ROLE_USER", "ROLE_REVIEWER"})
    @DeleteMapping("/answer/comment/{commentId}")
    public ResponseEntity removeComment_Answer(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long commentId
    ) {
        User user = userDetails.getUser();
        commentService.removeComment_Answer(user, commentId);

        return ResponseEntity.ok().body("댓글삭제 완료");
    }
}
