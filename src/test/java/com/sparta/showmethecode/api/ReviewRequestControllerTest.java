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
        user = new User("user", passwordEncoder.encode("password"), "?????????_?????????", UserRole.ROLE_USER, 0, 0, 0.0);
        reviewer = new User("reviewer", passwordEncoder.encode("password"), "?????????_?????????", UserRole.ROLE_REVIEWER, 0, 0, 0.0, Arrays.asList(new Language("JAVA")));

        userRepository.saveAll(Arrays.asList(user, reviewer));

        reviewRequest = new ReviewRequest(user, reviewer, "??????", "??????", ReviewRequestStatus.UNSOLVE, "JAVA");
        reviewRequestRepository.save(reviewRequest);

        ReviewRequestComment reviewRequestComment1 = new ReviewRequestComment("??????1", user);
        ReviewRequestComment reviewRequestComment2 = new ReviewRequestComment("??????2", reviewer);
        reviewRequestCommentRepository.saveAll(Arrays.asList(reviewRequestComment1, reviewRequestComment2));

        ReviewAnswer reviewAnswer = new ReviewAnswer("????????????", 4.5, reviewer, reviewRequest);
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
    @DisplayName("1. ???????????? ??????")
    @Test
    public void ????????????_??????() throws Exception {
        ReviewRequestDto reviewRequestDto = new ReviewRequestDto("?????????_??????", "?????????_??????", "JAVA", reviewer.getId());
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
                                        fieldWithPath("title").description("?????????????????? ??????"),
                                        fieldWithPath("content").description("?????????????????? ??????"),
                                        fieldWithPath("language").description("?????????????????? ????????????"),
                                        fieldWithPath("reviewerId").description("?????????????????? ????????? ID")
                                )
                        )
                );
    }

    @Order(2)
    @DisplayName("2. ???????????? ???????????? ")
    @Test
    public void ????????????_????????????() throws Exception {

        mockMvc.perform(get("/questions")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("isAsc", "true")
                )
                .andExpect(status().isOk())
                .andDo(document("get-questions",
                                requestParameters(
                                        parameterWithName("page").description("?????? ????????? ??????").optional(),
                                        parameterWithName("size").description("????????? ??? ?????????").optional(),
                                        parameterWithName("sortBy").description("???????????? ?????? ??????").optional(),
                                        parameterWithName("isAsc").description("????????????").optional(),
                                        parameterWithName("query").description("?????????????????? ?????? ??????").optional()
                                )
                                , responseFields(
                                        fieldWithPath("totalPage").description("?????? ????????????").type(JsonFieldType.NUMBER),
                                        fieldWithPath("totalElements").description("?????? ?????????").type(JsonFieldType.NUMBER),
                                        fieldWithPath("page").description("??????????????? ??????").type(JsonFieldType.NUMBER),
                                        fieldWithPath("size").description("????????? ??? ?????????").type(JsonFieldType.NUMBER),

                                        subsectionWithPath("data").description("????????????_?????????"),
                                        fieldWithPath("data.[].reviewRequestId").description("????????????_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("data.[].username").description("???????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].nickname").description("???????????????_?????????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].title").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].content").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].languageName").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].status").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].createdAt").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("data.[].commentCount").description("????????????_?????????").type(JsonFieldType.NUMBER)
                                )
                        )
                );
    }

    @Order(3)
    @DisplayName("3. ???????????? ?????? ???????????? (????????????) ")
    @Test
    public void ????????????_??????() throws Exception {

        mockMvc.perform(get("/question")
                        .param("id", reviewRequest.getId().toString())
                ).andExpect(status().isOk())
                .andDo(document("get-question",
                                requestParameters(
                                        parameterWithName("id").description("????????????_ID")
                                ),
                                responseFields(
                                        fieldWithPath("reviewRequestId").description("????????????_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("answerUserId").description("???????????????_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("username").description("???????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("nickname").description("???????????????_?????????").type(JsonFieldType.STRING),
                                        fieldWithPath("title").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("content").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("languageName").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("status").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("createdAt").description("????????????_????????????").type(JsonFieldType.STRING),

                                        subsectionWithPath("reviewAnswer").description("????????????"),
                                        fieldWithPath("reviewAnswer.reviewAnswerId").description("???????????????_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("reviewAnswer.reviewRequestId").description("???????????????_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("reviewAnswer.username").description("???????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("reviewAnswer.nickname").description("???????????????_?????????").type(JsonFieldType.STRING),
                                        fieldWithPath("reviewAnswer.answerContent").description("????????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("reviewAnswer.point").description("????????????_??????").type(JsonFieldType.NUMBER),
                                        fieldWithPath("reviewAnswer.createdAt").description("????????????_????????????").type(JsonFieldType.STRING),


                                        subsectionWithPath("comments").description("??????"),
                                        fieldWithPath("comments.[].commentId").description("??????_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("comments.[].userId").description("??????_?????????_ID").type(JsonFieldType.NUMBER),
                                        fieldWithPath("comments.[].username").description("??????_?????????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("comments.[].nickname").description("??????_?????????_?????????").type(JsonFieldType.STRING),
                                        fieldWithPath("comments.[].content").description("??????_??????").type(JsonFieldType.STRING),
                                        fieldWithPath("comments.[].createdAt").description("??????_????????????").type(JsonFieldType.STRING)
                                )
                        )
                );
    }

    @Order(4)
    @DisplayName("4. ???????????? ?????? ??????")
    @Test
    public void ????????????_??????() throws Exception {
        UpdateReviewDto updateReviewDto = new UpdateReviewDto("????????????", "????????????");
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
                                        parameterWithName("id").description("????????????_ID")
                                ),
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT token")
                                ),
                                requestFields(
                                        fieldWithPath("title").description("???????????????_??????_??????"),
                                        fieldWithPath("content").description("???????????????_??????_??????")
                                )
                        )
                );
    }

    @Order(5)
    @DisplayName("5. ???????????? ?????? ??????")
    @Test
    public void ????????????_??????() throws Exception {
        String token = createTokenAndSpringSecuritySetting();

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/question/{id}", reviewRequest.getId())
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                ).andExpect(status().isOk())
                .andDo(document("delete-question",
                                pathParameters(
                                        parameterWithName("id").description("????????????_ID")
                                ),
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT token")
                                )
                        )
                );
    }

    @Order(5)
    @DisplayName("5. ???????????? ?????? ????????? ????????? API ?????????")
    @Test
    public void ?????????_?????????() throws Exception {
        mockMvc.perform(get("/question/language")
                        .param("language", "JAVA")
                        .param("page", "1")
                        .param("size", "10")
                        .param("isAsc", "true")
                ).andExpect(status().isOk())
                .andDo(document("get-question-language-count",
                                requestParameters(
                                        parameterWithName("language").description("????????????"),
                                        parameterWithName("page").description("??????_?????????_??????").optional(),
                                        parameterWithName("size").description("?????????_???_?????????").optional(),
                                        parameterWithName("isAsc").description("????????????").optional()
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



