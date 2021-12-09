package com.sparta.showmethecode.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ReviewAnswerDto {
    private String content;
    private Long reviewerId;

}
