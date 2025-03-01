package com.j30n.stoblyx.support.docs;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;

import java.util.Collections;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

/**
 * REST Docs 문서화를 위한 유틸리티 클래스
 */
public class RestDocsUtils {

    /**
     * API 공통 응답 필드를 정의합니다.
     */
    public static FieldDescriptor[] getCommonResponseFields() {
        return new FieldDescriptor[] {
            fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS/ERROR)"),
            fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
        };
    }
    
    /**
     * API 공통 응답에 데이터 필드가 있는 경우 사용합니다.
     */
    public static FieldDescriptor[] getCommonResponseFieldsWithData() {
        return new FieldDescriptor[] {
            fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS/ERROR)"),
            fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터")
        };
    }

    /**
     * API 페이징 응답에 사용되는 공통 필드를 정의합니다.
     */
    public static FieldDescriptor[] getPageResponseFields() {
        return new FieldDescriptor[] {
            fieldWithPath("data.pageable").type(JsonFieldType.OBJECT).description("페이지 정보"),
            fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
            fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 존재 여부"),
            fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
            fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
            fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("페이지 오프셋"),
            fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
            fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
            fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
            fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이징 여부"),
            fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
            fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
            fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
            fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
            fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
            fieldWithPath("data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
            fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 존재 여부"),
            fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
            fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
            fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
            fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소 수"),
            fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("페이지 결과 존재 여부")
        };
    }

    /**
     * 페이지 요청 파라미터를 정의합니다.
     */
    public static ParameterDescriptor[] getPageRequestParameters() {
        return new ParameterDescriptor[] {
            parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
            parameterWithName("size").description("페이지 크기").optional(),
            parameterWithName("sort").description("정렬 방식 (예: createdAt,desc)").optional()
        };
    }
    
    /**
     * 공통 응답 필드를 위한 스니펫 생성
     * result, message 필드 포함
     */
    public static ResponseFieldsSnippet getResponseFieldsSnippet() {
        return responseFields(getCommonResponseFields());
    }

    /**
     * 데이터를 포함한 공통 응답 필드를 위한 스니펫 생성
     * result, message, data 필드 포함
     */
    public static ResponseFieldsSnippet getResponseFieldsSnippetWithData() {
        return responseFields(
                fieldWithPath("result").type(JsonFieldType.STRING).description("처리 결과 (SUCCESS/ERROR)"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                subsectionWithPath("data").description("응답 데이터")
        );
    }
    
    /**
     * 테스트에 사용할 인증된 사용자를 생성합니다.
     */
    public static RequestPostProcessor getTestUser() {
        // 테스트용 UserPrincipal 생성
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .role("USER")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        return SecurityMockMvcRequestPostProcessors.user(userPrincipal);
    }
} 