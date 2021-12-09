package com.sparta.showmethecode.service;

import com.sparta.showmethecode.domain.ReviewRequestStatus;
import com.sparta.showmethecode.dto.response.PageResponseDto;
import com.sparta.showmethecode.security.JwtUtils;
import com.sparta.showmethecode.security.UserDetailsImpl;
import com.sparta.showmethecode.security.UserDetailsServiceImpl;
import com.sparta.showmethecode.domain.Language;
import com.sparta.showmethecode.domain.User;
import com.sparta.showmethecode.domain.UserRole;
import com.sparta.showmethecode.dto.request.SigninRequestDto;
import com.sparta.showmethecode.dto.request.SignupRequestDto;
import com.sparta.showmethecode.dto.response.ReviewRequestResponseDto;
import com.sparta.showmethecode.dto.response.ReviewerInfoDto;
import com.sparta.showmethecode.dto.response.SigninResponseDto;
import com.sparta.showmethecode.repository.LanguageRepository;
import com.sparta.showmethecode.repository.ReviewAnswerRepository;
import com.sparta.showmethecode.repository.ReviewRequestRepository;
import com.sparta.showmethecode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ReviewRequestRepository reviewRequestRepository;
    private final ReviewAnswerRepository reviewAnswerRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final LanguageRepository languageRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User saveUser(SignupRequestDto requestDto) {
        UserRole userRole = requestDto.isReviewer() ? UserRole.ROLE_REVIEWER : UserRole.ROLE_USER;

        User user = User.builder()
                .username(requestDto.getUsername())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .nickname(requestDto.getNickname())
                .role(userRole)
                .evalCount(0)
                .evalCount(0)
                .answerCount(0)
                .languages(new ArrayList<>())
                .build();

        User savedUser = userRepository.save(user);

        if (requestDto.getLanguages().size() > 0) {
            Set<String> languages = requestDto.getLanguages();
            for (String l : languages) {
                Language language = new Language(l.toUpperCase());
                language.setUser(savedUser);
                languageRepository.save(language);
            }
        }

        return savedUser;
    }

    public SigninResponseDto signin(SigninRequestDto requestDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("로그인에 실패했습니다.");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(requestDto.getUsername());
        String token = jwtUtils.createToken(userDetails.getUsername());

        String authority = userDetails.getAuthorities().stream().findFirst().get().toString();

        return new SigninResponseDto(userDetails.getUser().getId(), token, authority, HttpStatus.CREATED, "로그인에 성공했습니다.");
    }

    public List<String> getMyLanguage(Long userId) {
        return languageRepository.findByUserId(userId);
    }


    /**
     * 언어이름으로 리뷰어 조회 API
     */
    public List<ReviewerInfoDto> findReviewerByLanguage(String languageName) {
        List<User> reviewers = userRepository.findReviewerByLanguage(languageName.toUpperCase());
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        return reviewers.stream().map(
                r -> new ReviewerInfoDto(
                        r.getId(),
                        r.getUsername(),
                        r.getNickname(),
                        r.getLanguages().stream().map(l -> new String(l.getName())).collect(Collectors.toList()),
                        r.getAnswerCount(),
                        r.getEvalCount() == 0 ? 0 : Double.valueOf(decimalFormat.format(r.getEvalTotal() / r.getEvalCount())))
        ).collect(Collectors.toList());
    }

    /**
     * 내가 등록한 리뷰요청목록 조회 API
     */
    public PageResponseDto<ReviewRequestResponseDto> getMyReviewRequestList(User user, int page, int size, String sortBy, boolean isAsc, ReviewRequestStatus status) {
        Pageable pageable = makePageable(page, size, sortBy, isAsc);

        Page<ReviewRequestResponseDto> reviewRequests = reviewRequestRepository.findMyReviewRequestList(user.getId(), pageable, status);

        log.info("getMyReviewRequestList = {}", reviewRequests.getContent());

        return new PageResponseDto<ReviewRequestResponseDto>(
                reviewRequests.getContent(),
                reviewRequests.getTotalPages(),
                reviewRequests.getTotalElements(),
                page, size
        );
    }

    private Pageable makePageable(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);

        return PageRequest.of(page, size, sort);
    }
}
