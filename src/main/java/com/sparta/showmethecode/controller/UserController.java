package com.sparta.showmethecode.controller;

import com.sparta.showmethecode.domain.ReviewRequestStatus;
import com.sparta.showmethecode.dto.request.EvaluateAnswerDto;
import com.sparta.showmethecode.security.UserDetailsImpl;
import com.sparta.showmethecode.domain.User;
import com.sparta.showmethecode.dto.request.SigninRequestDto;
import com.sparta.showmethecode.dto.request.SignupRequestDto;
import com.sparta.showmethecode.dto.response.*;
import com.sparta.showmethecode.service.ReviewerService;
import com.sparta.showmethecode.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/user/signup")
    public ResponseEntity<BasicResponseDto> signup(@Valid @RequestBody SignupRequestDto requestDto, Errors error) {

        if (error.hasErrors()) {

            String message = "";

            Map<String, String> errors = new HashMap<>();
            for (FieldError value : error.getFieldErrors()) {
                errors.put(value.getField(), value.getDefaultMessage());
                message = value.getDefaultMessage();
                System.out.println(value.getDefaultMessage());
            }


            BasicResponseDto responseDto = BasicResponseDto.builder()
                    .result("fail").httpStatus(HttpStatus.FORBIDDEN).message(message).build();

            return new ResponseEntity<>(responseDto, HttpStatus.FORBIDDEN);

        }

        userService.saveUser(requestDto);
        BasicResponseDto responseDto = BasicResponseDto.builder()
                .result("success").httpStatus(HttpStatus.CREATED).message("회원가입에 성공했습니다.").build();


        return ResponseEntity.ok(responseDto);
    }


    @PostMapping("/user/signin")
    public SigninResponseDto signin(@RequestBody SigninRequestDto requestDto) {

        return userService.signin(requestDto);
    }

    /**
     * 해당 언어의 리뷰어 조회 API
     */
    @GetMapping("/user/language")
    public ResponseEntity findReviewerByLanguage(@RequestParam String language) {
        log.info("findReviewerByLanguage language = {}", language);
        List<ReviewerInfoDto> reviewerInfoList = userService.findReviewerByLanguage(language);
        return ResponseEntity.ok().body(reviewerInfoList);
    }


    /**
     * 로그아웃 API
     */
    @Secured({"ROLE_USER", "ROLE_REVIEWER"})
    @PostMapping("/user/logout")
    public BasicResponseDto logout() {
        SecurityContextHolder.clearContext();

        return new BasicResponseDto(null, "success", "로그아웃 완료", HttpStatus.OK);
    }

    /**
     * 내가 등록한 리뷰요청목록 조회 API
     */
    @Secured({"ROLE_USER", "ROLE_REVIEWER"})
    @GetMapping("/user/requests")
    public ResponseEntity<PageResponseDto> getMyRequestList(
            @RequestParam ReviewRequestStatus status,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "true") Boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        --page;

        log.info("getMyRequestList status = {}", status);

        User user = userDetails.getUser();
        PageResponseDto response = userService.getMyReviewRequestList(user, page, size, sortBy, isAsc, status);

        return ResponseEntity.ok(response);
    }
}
