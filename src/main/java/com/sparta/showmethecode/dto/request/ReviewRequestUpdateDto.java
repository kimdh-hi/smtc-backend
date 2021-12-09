package com.sparta.showmethecode.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class ReviewRequestUpdateDto {

    private String title;
    private String content;

}
