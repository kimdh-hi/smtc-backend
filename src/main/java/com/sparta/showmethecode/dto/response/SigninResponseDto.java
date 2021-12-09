package com.sparta.showmethecode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SigninResponseDto {
    private Long id;
    private String token;
    private String authority;
    private HttpStatus httpStatus;
    private String message;
}
