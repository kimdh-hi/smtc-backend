package com.sparta.showmethecode.repository;

import com.sparta.showmethecode.domain.ReviewAnswer;

import com.sparta.showmethecode.repository.querydsl.ReviewAnswerDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewAnswerRepository extends JpaRepository<ReviewAnswer, Long>, ReviewAnswerDao {

}
