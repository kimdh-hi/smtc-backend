package com.sparta.showmethecode.api;

import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sparta.showmethecode.domain.*;
import com.sparta.showmethecode.dto.request.ReviewRequestDto;
import com.sparta.showmethecode.dto.request.UpdateReviewDto;
import com.sparta.showmethecode.repository.ReviewAnswerRepository;
import com.sparta.showmethecode.repository.ReviewRequestCommentRepository;
import com.sparta.showmethecode.repository.ReviewRequestRepository;
import com.sparta.showmethecode.repository.UserRepository;
import com.sparta.showmethecode.security.JwtUtils;
import com.sparta.showmethecode.security.UserDetailsImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest(properties = "spring.config.location=" +
        "classpath:/application-test.yml")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class ReviewRequestControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ReviewRequestRepository reviewRequestRepository;
    @Autowired
    ReviewRequestCommentRepository reviewRequestCommentRepository;
    @Autowired
    ReviewAnswerRepository reviewAnswerRepository;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    PasswordEncoder passwordEncoder;

    final String TOKEN_PREFIX = "Bearer ";

    User user;
    User reviewer;
    ReviewRequest reviewRequest;
    String token;

    @BeforeAll
    void init() {
        user = new User("user", passwordEncoder.encode("password"), "테스트_사용자", UserRole.ROLE_USER, 0, 0, 0.0);
        reviewer = new User("reviewer", passwordEncoder.encode("password"), "테스트_리뷰어", UserRole.ROLE_REVIEWER, 0, 0, 0.0, Arrays.asList(new Language("JAVA")));

        userRepository.saveAll(Arrays.asList(user, reviewer));

        reviewRequest = new ReviewRequest(user, reviewer, "제목", "내용", ReviewRequestStatus.UNSOLVE, "JAVA");
        reviewRequestRepository.save(reviewRequest);

        ReviewRequestComment reviewRequestComment1 = new ReviewRequestComment("댓글1", user);
        ReviewRequestComment reviewRequestComment2 = new ReviewRequestComment("댓글2", reviewer);
        reviewRequestCommentRepository.saveAll(Arrays.asList(reviewRequestComment1, reviewRequestComment2));

        ReviewAnswer reviewAnswer = new ReviewAnswer("답변내용", 4.5, reviewer, reviewRequest);
        reviewAnswerRepository.save(reviewAnswer);

        reviewRequest.addComment(reviewRequestComment1);
        reviewRequest.addComment(reviewRequestComment2);
        reviewRequest.setReviewAnswer(reviewAnswer);

        reviewRequestRepository.save(reviewRequest);

    }

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(MockMvcResultHandlers.print())
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Order(1)
    @DisplayName("1. 코드리뷰 요청")
    @Test
    public void 코드리뷰_요청() throws Exception {
        ReviewRequestDto reviewRequestDto = new ReviewRequestDto("테스트_제목", "테스트_내용", "JAVA", reviewer.getId());
        String dto = new GsonBuilder().create().toJson(reviewRequestDto);

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        String token = jwtUtils.createToken(userDetails.getUsername());

        mockMvc.perform(post("/question")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(dto))
                .andExpect(status().isOk())
                .andDo(document("post-question",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT token")
                                ),
                                requestFields(
                                        fieldWithPath("title").description("코드리뷰요청 제목"),
                                        fieldWithPath("content").description("코드리뷰요청 내용"),
                                        fieldWithPath("language").description("코드리뷰요청 언어이름"),
                                        fieldWithPath("reviewerId").description("코드리뷰요청 리뷰어 ID")
                                )
                        )
                );
    }

    @Order(2)
    @DisplayName("2. 코드리뷰 요청목록 ")
    @Test
    public void 코드리뷰_요청목록() throws Exception {

        mockMvc.perform(get("/questions")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("isAsc", "true")
                )
                .andExpect(status().isOk())
                .andDo(document("get-questions",
                                requestParameters(
                                        parameterWithName("page").description("요청 페이지 번호").optional(),
                                        parameterWithName("size").description("페이지 당 요소수").optional(),
                                        parameterWithName("sortBy").description("정렬기준 필드 이름").optional(),
                                        parameterWithName("isAsc").description("정렬방향").optional(),
                                        parameterWithName("query").description("코드리뷰요청 목록 검색").optional()
                                )
                                , responseFields(
                                        fieldWithPath("totalPage").description("전체 페이지수").type(JsonFieldType.NUMBER),
                                        fieldWithPath("totalElements").description("전체 요소수").type(JsonFieldType.NUMBER),
                                        fieldWithPath("page").description("현재페이지 번호").type(JsonFieldType.NUMBER),
                                        fieldWithPath("size").description("페이지 당 요소수").type(JsonFieldType.NUMBER),

                                        subsectionWithPath("data").description("리뷰요청_데이터"),
                                        fieldWithPath("data.[].reviewRequestId").description("리뷰요청_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("data.[].username").description("리뷰요청자_이름").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].nickname").description("리뷰요청자_닉네임").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].title").description("리뷰요청_제목").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].content").description("리뷰요청_내용").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].languageName").description("리뷰요청_언어").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].status").description("리뷰요청_상태").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].createdAt").description("리뷰요청_날짜").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].commentCount").description("리뷰요청_댓글수").type(JsonFieldType.NUMBER)
                                )
                        )
                );
    }

    @Order(3)
    @DisplayName("3. 코드리뷰 요청 상세정보 (단건조회) ")
    @Test
    public void 상세정보_조회() throws Exception {

        mockMvc.perform(get("/question")
                        .param("id", reviewRequest.getId().toString())
                ).andExpect(status().isOk())
                .andDo(document("get-question",
                                requestParameters(
                                        parameterWithName("id").description("리뷰요청_ID")
                                ),
                                responseFields(
                                        fieldWithPath("reviewRequestId").description("리뷰요청_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("answerUserId").description("리뷰답변자_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("username").description("리뷰요청자_이름").type(JsonFieldType.STRING),
                                        fieldWithPath("nickname").description("리뷰요청자_닉네임").type(JsonFieldType.STRING),
                                        fieldWithPath("title").description("리뷰요청_제목").type(JsonFieldType.STRING),
                                        fieldWithPath("content").description("리뷰요청_내용").type(JsonFieldType.STRING),
                                        fieldWithPath("languageName").description("리뷰요청_언어").type(JsonFieldType.STRING),
                                        fieldWithPath("status").description("리뷰요청_상태").type(JsonFieldType.STRING),
                                        fieldWithPath("createdAt").description("리뷰요청_생성날짜").type(JsonFieldType.STRING),

                                        subsectionWithPath("reviewAnswer").description("리뷰답변"),
                                        fieldWithPath("reviewAnswer.reviewAnswerId").description("리뷰답변자_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("reviewAnswer.reviewRequestId").description("리뷰요청자_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("reviewAnswer.username").description("리뷰답변자_이름").type(JsonFieldType.STRING),
                                        fieldWithPath("reviewAnswer.nickname").description("리뷰답변자_닉네임").type(JsonFieldType.STRING),
                                        fieldWithPath("reviewAnswer.answerContent").description("리뷰답변_내용").type(JsonFieldType.STRING),
                                        fieldWithPath("reviewAnswer.point").description("리뷰답변_점수").type(JsonFieldType.NUMBER),
                                        fieldWithPath("reviewAnswer.createdAt").description("리뷰답변_생성날짜").type(JsonFieldType.STRING),


                                        subsectionWithPath("comments").description("댓글"),
                                        fieldWithPath("comments.[].commentId").description("댓글_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("comments.[].userId").description("댓글_작성자_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("comments.[].username").description("댓글_작성자_이름").type(JsonFieldType.STRING),
                                        fieldWithPath("comments.[].nickname").description("댓글_작성자_닉네임").type(JsonFieldType.STRING),
                                        fieldWithPath("comments.[].content").description("댓글_내용").type(JsonFieldType.STRING),
                                        fieldWithPath("comments.[].createdAt").description("댓글_생성날짜").type(JsonFieldType.STRING)
                                )
                        )
                );
    }

    @Order(4)
    @DisplayName("4. 코드리뷰 요청 수정")
    @Test
    public void 코드리뷰_수정() throws Exception {
        UpdateReviewDto updateReviewDto = new UpdateReviewDto("제목수정", "내용수정");
        String dtoJson = new Gson().toJson(updateReviewDto);

        String token = createTokenAndSpringSecuritySetting();

        mockMvc.perform(RestDocumentationRequestBuilders.put("/question/{id}", reviewRequest.getId())
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(dtoJson)
                ).andExpect(status().isOk())
                .andDo(document("put-question",
                                pathParameters(
                                        parameterWithName("id").description("리뷰요청_ID")
                                ),
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT token")
                                ),
                                requestFields(
                                        fieldWithPath("title").description("수정하고자_하는_제목"),
                                        fieldWithPath("content").description("수정하고자_하는_내용")
                                )
                        )
                );
    }

    @Order(5)
    @DisplayName("5. 코드리뷰 요청 삭제")
    @Test
    public void 코드리뷰_삭제() throws Exception {
        String token = createTokenAndSpringSecuritySetting();

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/question/{id}", reviewRequest.getId())
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                ).andExpect(status().isOk())
                .andDo(document("delete-question",
                                pathParameters(
                                        parameterWithName("id").description("리뷰요청_ID")
                                ),
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT token")
                                )
                        )
                );
    }

    @Order(5)
    @DisplayName("5. 코드리뷰 요청 언어별 카운팅 API 테스트")
    @Test
    public void 언어별_카운팅() throws Exception {
        mockMvc.perform(get("/question/language")
                        .param("language", "JAVA")
                        .param("page", "1")
                        .param("size", "10")
                        .param("isAsc", "true")
                ).andExpect(status().isOk())
                .andDo(document("get-question-language-count",
                                requestParameters(
                                        parameterWithName("language").description("언어이름"),
                                        parameterWithName("page").description("요청_페이지_번호").optional(),
                                        parameterWithName("size").description("페이지_당_요소수").optional(),
                                        parameterWithName("isAsc").description("정렬방향").optional()
                                )
                        )
                );
    }

    private String createTokenAndSpringSecuritySetting() {
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        return token = jwtUtils.createToken(userDetails.getUsername());
    }

}



