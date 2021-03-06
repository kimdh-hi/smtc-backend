package com.sparta.showmethecode.api;

import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;
import com.sparta.showmethecode.domain.*;
import com.sparta.showmethecode.dto.request.AddAnswerDto;
import com.sparta.showmethecode.dto.request.EvaluateAnswerDto;
import com.sparta.showmethecode.dto.request.UpdateAnswerDto;
import com.sparta.showmethecode.dto.request.UpdateReviewerDto;
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

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.config.location=" +
        "classpath:/application-test.yml")
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class ReviewerControllerTest {
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
    User newReviewer;
    ReviewRequest reviewRequest;
    ReviewAnswer reviewAnswer;
    String token;

    @BeforeAll
    void init() {
        user = new User("user", passwordEncoder.encode("password"), "?????????_?????????", UserRole.ROLE_USER, 0, 0, 0.0);
        reviewer = new User("reviewer", passwordEncoder.encode("password"), "?????????_?????????", UserRole.ROLE_REVIEWER, 0, 0, 0.0, Arrays.asList(new Language("JAVA")));
        newReviewer = new User("newReviewer", passwordEncoder.encode("password"), "?????????_?????????", UserRole.ROLE_REVIEWER, 0, 0, 0.0, Arrays.asList(new Language("JAVA")));

        userRepository.saveAll(Arrays.asList(user, reviewer, newReviewer));

        reviewRequest = new ReviewRequest(user, reviewer, "??????", "??????", ReviewRequestStatus.UNSOLVE, "JAVA");
        reviewRequestRepository.save(reviewRequest);

        ReviewRequestComment reviewRequestComment1 = new ReviewRequestComment("??????1", user);
        ReviewRequestComment reviewRequestComment2 = new ReviewRequestComment("??????2", reviewer);
        reviewRequestCommentRepository.saveAll(Arrays.asList(reviewRequestComment1, reviewRequestComment2));

        reviewAnswer = new ReviewAnswer("????????????", 4.5, reviewer, reviewRequest);
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
    @DisplayName("1. ???????????? API ?????????")
    @Test
    public void ????????????() throws Exception {
        String token = createTokenAndSpringSecuritySetting(reviewer);

        AddAnswerDto addAnswerDto = new AddAnswerDto("????????????");
        String dtoJson = new Gson().toJson(addAnswerDto);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/answer/{questionId}", reviewRequest.getId())
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(dtoJson)
                ).andExpect(status().isOk())
                .andDo(document("post-answer",
                                pathParameters(
                                        parameterWithName("questionId").description("????????????_ID")
                                ),
                                requestFields(
                                        fieldWithPath("content").description("????????????")
                                )
                        )
                );
    }

    @Order(2)
    @DisplayName("2. ???????????? ?????? API")
    @Test
    public void ????????????_??????() throws Exception {
        String token = createTokenAndSpringSecuritySetting(reviewer);
        mockMvc.perform(RestDocumentationRequestBuilders.get("/answers")
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .param("page", "1")
                        .param("size", "10")
                        .param("isAsc", "true")
                        .param("sortBy", "createdAt")
                )
                .andExpect(status().isOk())
                .andDo(document("get-answers",
                                requestParameters(
                                        parameterWithName("page").description("??????_?????????_??????").optional(),
                                        parameterWithName("size").description("?????????_???_?????????").optional(),
                                        parameterWithName("sortBy").description("????????????_??????_??????").optional(),
                                        parameterWithName("isAsc").description("????????????").optional()
                                )
                                , responseFields(
                                        fieldWithPath("totalPage").description("?????? ????????????").type(JsonFieldType.NUMBER),
                                        fieldWithPath("totalElements").description("?????? ?????????").type(JsonFieldType.NUMBER),
                                        fieldWithPath("page").description("??????????????? ??????").type(JsonFieldType.NUMBER),
                                        fieldWithPath("size").description("????????? ??? ?????????").type(JsonFieldType.NUMBER),

                                        subsectionWithPath("data").description("??????_?????????"),
                                        fieldWithPath("data.[].reviewAnswerId").description("????????????_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("data.[].reviewRequestId").description("????????????_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("data.[].username").description("?????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].nickname").description("?????????_?????????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].answerContent").description("??????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].point").description("??????_??????").type(JsonFieldType.NUMBER),
                                        fieldWithPath("data.[].createdAt").description("??????_??????").type(JsonFieldType.STRING)
                                )
                        )
                );
    }

    @Order(3)
    @DisplayName("3. ???????????? API ?????????")
    @Test
    public void ????????????() throws Exception {
        String token = createTokenAndSpringSecuritySetting(reviewer);

        UpdateAnswerDto updateAnswerDto = new UpdateAnswerDto("????????????");
        String dtoJson = new Gson().toJson(updateAnswerDto);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/answer/{answerId}", reviewAnswer.getId())
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(dtoJson)
                ).andExpect(status().isOk())
                .andDo(document("put-answer",
                                requestFields(
                                        fieldWithPath("content").description("????????????")
                                ),
                                pathParameters(
                                        parameterWithName("answerId").description("????????????_ID")
                                )
                        )
                );
    }

    @Order(4)
    @DisplayName("4. ????????? ?????? API")
    @Test
    public void ?????????_??????() throws Exception {
        String token = createTokenAndSpringSecuritySetting(reviewer);

        UpdateReviewerDto updateReviewerDto = new UpdateReviewerDto(newReviewer.getId());
        String dtoJson = new Gson().toJson(updateReviewerDto);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/question/{questionId}/reviewer/{reviewerId}", reviewRequest.getId(), reviewer.getId())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(dtoJson)
                ).andExpect(status().isOk())
                .andDo(document("put-question-reviewer",
                                pathParameters(
                                        parameterWithName("questionId").description("????????????_ID"),
                                        parameterWithName("reviewerId").description("??????_?????????_ID")
                                ),
                                requestFields(
                                        fieldWithPath("newReviewerId").description("?????????_?????????_ID")
                                )
                        )
                );
    }

    @Order(5)
    @DisplayName("5. ???????????? API")
    @Test
    public void ????????????() throws Exception {

        String token = createTokenAndSpringSecuritySetting(user);
        EvaluateAnswerDto evaluateAnswerDto = new EvaluateAnswerDto(4.5);
        String dtoJson = new Gson().toJson(evaluateAnswerDto);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/question/{questionId}/eval/{answerId}", reviewRequest.getId(), reviewAnswer.getId())
                .header(HttpHeaders.AUTHORIZATION, token)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(dtoJson)
        ).andExpect(status().isOk())
                .andDo(document("post-answer-evaluate",
                                pathParameters(
                                        parameterWithName("questionId").description("????????????_ID"),
                                        parameterWithName("answerId").description("????????????_ID")
                                ),
                                requestFields(
                                        fieldWithPath("point").description("????????????")
                                )
                        )
                );
    }


    private String createTokenAndSpringSecuritySetting(User user) {
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        return token = jwtUtils.createToken(userDetails.getUsername());
    }
}
