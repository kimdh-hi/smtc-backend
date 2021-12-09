package com.sparta.showmethecode.repository;

import com.sparta.showmethecode.domain.ReviewRequest;
import com.sparta.showmethecode.domain.ReviewRequestComment;
import com.sparta.showmethecode.domain.User;
import com.sparta.showmethecode.repository.querydsl.ReviewRequestCommentDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRequestCommentRepository extends JpaRepository<ReviewRequestComment, Long>, ReviewRequestCommentDao {

    ReviewRequestComment findByIdAndUser(Long id, User user);
    void deleteByReviewRequestAndUser(ReviewRequest reviewRequest, User user);
}
