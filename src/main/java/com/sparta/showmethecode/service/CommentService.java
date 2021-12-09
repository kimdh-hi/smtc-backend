package com.sparta.showmethecode.service;

import com.sparta.showmethecode.domain.*;
import com.sparta.showmethecode.dto.request.AddCommentDto;
import com.sparta.showmethecode.dto.request.UpdateCommentDto;
import com.sparta.showmethecode.repository.ReviewAnswerCommentRepository;
import com.sparta.showmethecode.repository.ReviewAnswerRepository;
import com.sparta.showmethecode.repository.ReviewRequestCommentRepository;
import com.sparta.showmethecode.repository.ReviewRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class CommentService {

    private final ReviewRequestRepository reviewRequestRepository;
    private final ReviewRequestCommentRepository reviewRequestCommentRepository;

    private final ReviewAnswerRepository reviewAnswerRepository;
    private final ReviewAnswerCommentRepository reviewAnswerCommentRepository;

    /**
     * 코드리뷰요청 - 댓글추가 API
     */
    @Transactional
    public void addComment_Question(User user, Long questionId, AddCommentDto addCommentDto) {
        ReviewRequest reviewRequest = reviewRequestRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰요청입니다."));
        ReviewRequestComment reviewRequestComment = new ReviewRequestComment(addCommentDto.getContent(), user);
        reviewRequest.addComment(reviewRequestComment);
    }

    /**
     * 코드리뷰요청 - 댓글삭제 API
     */
    @Transactional
    public long removeComment_Question(User user, Long commentId) {
        return reviewRequestCommentRepository.deleteComment(user.getId(), commentId);
    }

    /**
     * 코드리뷰요청 - 댓글수정 API
     */
    @Transactional
    public void updateComment_Question(User user, Long commentId, UpdateCommentDto updateCommentDto) {
        ReviewRequestComment reviewRequestComment = reviewRequestCommentRepository.findByIdAndUser(commentId, user);
        reviewRequestComment.update(updateCommentDto);
    }

    /**
     * 리뷰답변 - 댓글추가 API
     */
    public void addComment_Answer(User user, Long answerId, AddCommentDto addCommentDto) {
        ReviewAnswer reviewAnswer = reviewAnswerRepository.findById(answerId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 답변입니다.")
        );
        ReviewAnswerComment reviewAnswerComment = new ReviewAnswerComment(addCommentDto.getContent(), user, reviewAnswer);
        reviewAnswer.addComment(reviewAnswerComment);
    }

    /**
     * 리뷰답변 - 댓글수정 API
     */
    public void updateComment_Answer(User user, Long commentId, UpdateCommentDto updateCommentDto) {
        ReviewAnswerComment reviewAnswerComment = reviewAnswerCommentRepository.findByIdAndUser(commentId, user);
        reviewAnswerComment.update(updateCommentDto);
    }

    /**
     * 리뷰답변 - 댓글삭제 API
     */
    public void removeComment_Answer(User user, Long commentId) {
        reviewAnswerCommentRepository.deleteByUserAndId(user, commentId);
    }
}
