package com.sparta.showmethecode.domain;

import com.sparta.showmethecode.dto.request.UpdateCommentDto;
import com.sparta.showmethecode.dto.request.UpdateReviewDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 리뷰 답변서에 달리는 댓글
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class ReviewAnswerComment extends Timestamped {

    @Id @GeneratedValue
    private Long id;

    private String content;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "review_answer_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ReviewAnswer reviewAnswer;

    public ReviewAnswerComment(String content, User user, ReviewAnswer reviewAnswer) {
        this.content = content;
        this.user = user;
        this.reviewAnswer = reviewAnswer;
    }

    public void setReviewAnswer(ReviewAnswer reviewAnswer) {
        this.reviewAnswer = reviewAnswer;
    }

    public void update(UpdateCommentDto dto) {
        this.content = dto.getContent();
    }
}
