package com.sparta.showmethecode.repository;

import com.sparta.showmethecode.domain.ReviewRequest;
import com.sparta.showmethecode.domain.User;
import com.sparta.showmethecode.repository.querydsl.ReviewRequestDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRequestRepository extends JpaRepository<ReviewRequest, Long>, ReviewRequestDao {

    Page<ReviewRequest> findAll(Pageable pageable);

    Page<ReviewRequest> findByAnswerUser(User answerUser, Pageable pageable);

    // 테스트
    List<ReviewRequest> findByTitle(String title);

    List<ReviewRequest> findByAnswerUser(Long id);
}
