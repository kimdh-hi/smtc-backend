package com.sparta.showmethecode.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UpdateReviewDto {
    private String title;
    private String content;
}
