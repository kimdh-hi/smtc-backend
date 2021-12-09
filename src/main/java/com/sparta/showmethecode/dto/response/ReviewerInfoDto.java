package com.sparta.showmethecode.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@Data
public class ReviewerInfoDto {

    private Long id;
    private String username;
    private String nickname;
    private List<String> languages = new ArrayList<>();

    private int answerCount;
    private double point;

    @QueryProjection
    public ReviewerInfoDto(Long id, String username, String nickname, List<String> languages, int answerCount, double point) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.languages = languages;
        this.answerCount = answerCount;
        this.point = point;
    }
}
