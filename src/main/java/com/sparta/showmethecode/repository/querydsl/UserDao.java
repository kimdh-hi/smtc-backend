package com.sparta.showmethecode.repository.querydsl;

import com.sparta.showmethecode.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserDao {

    // 언어별 리뷰어 조회
    List<User> findReviewerByLanguage(String language);
    // 리뷰어 랭킹 조회
    Page<User> getReviewerRanking(Pageable pageable, boolean isAsc);
    // 리부어 랭킹 조회 5위까지
    List<User> getReviewerRankingTop5(boolean isDesc);
}
