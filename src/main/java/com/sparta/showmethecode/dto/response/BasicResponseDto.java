package com.sparta.showmethecode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BasicResponseDto {

    private Long id;
    private String result;
    private String message;
    private HttpStatus httpStatus;
}
