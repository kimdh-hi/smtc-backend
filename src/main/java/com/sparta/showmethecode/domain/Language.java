package com.sparta.showmethecode.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 리뷰어가 어떤 프로그래밍 언어를 다루는지에 대한 테이블
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Language extends Timestamped{

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public Language(String name) {
        this.name = name;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
