package com.sparta.showmethecode.repository;

import com.sparta.showmethecode.domain.ReviewAnswerComment;
import com.sparta.showmethecode.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewAnswerCommentRepository extends JpaRepository<ReviewAnswerComment, Long> {

    ReviewAnswerComment findByIdAndUser(Long id, User user);

    void deleteByUserAndId(User user, Long id);
}
