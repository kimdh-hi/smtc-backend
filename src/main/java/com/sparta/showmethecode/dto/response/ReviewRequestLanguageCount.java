package com.sparta.showmethecode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@ToString
@AllArgsConstructor
@Data
public class ReviewRequestLanguageCount {
    private String languageName;
    private long count;
}
