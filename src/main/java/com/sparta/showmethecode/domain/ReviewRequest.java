package com.sparta.showmethecode.domain;

import com.sparta.showmethecode.dto.request.ReviewRequestUpdateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * 코드리뷰 요청서
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class ReviewRequest extends Timestamped {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private ReviewRequestStatus status;

    @Column(nullable = false)
    private String languageName;

    // 한 명의 사용자는 여러 개 코드리뷰 요청서를 작성할 수 있다.
    @JoinColumn(name = "request_user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User requestUser;

    // 한 명의 리뷰어는 여러 개 코드리뷰 요청서에 리뷰를 할 수 있다.
    @JoinColumn(name = "answer_user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User answerUser;

    @JoinColumn(name = "review_answer_id")
    @OneToOne(fetch = FetchType.LAZY)
    private ReviewAnswer reviewAnswer;

    @OneToMany(mappedBy = "reviewRequest", cascade = CascadeType.ALL)
    private List<ReviewRequestComment> reviewRequestComments = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.REMOVE)
    private List<Notification> notifications = new ArrayList<>();

    public void addComment(ReviewRequestComment comment) {
        this.reviewRequestComments.add(comment);
        comment.setReviewRequest(this);
    }

    public ReviewRequest(User requestUser, String title, String content, ReviewRequestStatus status, String languageName) {
        this.requestUser = requestUser;
        this.title = title;
        this.content = content;
        this.status = status;
        this.languageName = languageName.toUpperCase();
    }

    public ReviewRequest(User requestUser, User answerUser,String title, String content, ReviewRequestStatus status, String languageName) {
        this.requestUser = requestUser;
        this.answerUser = answerUser;
        this.title = title;
        this.content = content;
        this.status = status;
        this.languageName = languageName.toUpperCase();
    }

    public void update(ReviewRequestUpdateDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
    }

    public void setStatus(ReviewRequestStatus status) {
        this.status = status;
    }

    public boolean hasComments() {
        if (!Objects.isNull(this.reviewRequestComments))
            return this.getReviewRequestComments().size() > 0 ? true : false;
        else
            return false;
    }

    public void setReviewAnswer(ReviewAnswer reviewAnswer) {
        this.reviewAnswer = reviewAnswer;
        reviewAnswer.setReviewRequest(this);
    }

    public void updateReviewer(User answerUser) {
        this.answerUser = answerUser;
    }
}
