package com.sparta.showmethecode.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum ReviewRequestStatus {

    UNSOLVE("미해결"),
    CHECKED("확인됨"),
    REJECTED("거절됨"),
    SOLVE("해결됨"),

    EVALUATED("평가됨"),
    ALL("ALL");

    private final String description;

    ReviewRequestStatus(String description) {
        this.description = description;
    }
}
