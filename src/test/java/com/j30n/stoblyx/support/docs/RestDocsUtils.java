package com.j30n.stoblyx.support.docs;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
            fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS/ERROR)"),
            fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
            fieldWithPath("data.content").type(JsonFieldType.ARRAY).description("페이지 내용"),
            fieldWithPath("data.pageable").type(JsonFieldType.OBJECT).description("페이징 정보"),
            fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
            fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("페이지 오프셋"),
            fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"),
            fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
            fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
            fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이징 여부"),
            fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
            fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
            fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
            fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
            fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
            fieldWithPath("data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
            fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
            fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
            fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
            fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소 수"),
            fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("페이지 결과 존재 여부")
        };
    }
    
    /**
     * 북마크 목록 페이지 응답 필드를 정의합니다.
     */
    public static FieldDescriptor[] getBookmarkPageResponseFields() {
        List<FieldDescriptor> fieldDescriptors = new ArrayList<>();
        
        // 기본 응답 필드 추가
        Collections.addAll(fieldDescriptors, getCommonResponseFieldsWithData());
        
        // 페이지네이션 관련 필드 추가
        fieldDescriptors.add(fieldWithPath("data.content").type(JsonFieldType.ARRAY).description("북마크 목록"));
        fieldDescriptors.add(fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"));
        fieldDescriptors.add(fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"));
        fieldDescriptors.add(fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"));
        fieldDescriptors.add(fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 존재 여부"));
        fieldDescriptors.add(fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"));
        fieldDescriptors.add(fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"));
        fieldDescriptors.add(fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("페이지 오프셋"));
        fieldDescriptors.add(fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"));
        fieldDescriptors.add(fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이징 여부"));
        fieldDescriptors.add(fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 항목 수"));
        fieldDescriptors.add(fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"));
        fieldDescriptors.add(fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"));
        fieldDescriptors.add(fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"));
        fieldDescriptors.add(fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"));
        fieldDescriptors.add(fieldWithPath("data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"));
        fieldDescriptors.add(fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 존재 여부"));
        fieldDescriptors.add(fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"));
        fieldDescriptors.add(fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"));
        fieldDescriptors.add(fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"));
        fieldDescriptors.add(fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 항목 수"));
        fieldDescriptors.add(fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("페이지 결과 존재 여부"));
        
        // 북마크 콘텐츠 항목 필드 추가
        fieldDescriptors.add(fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("북마크 ID"));
        fieldDescriptors.add(fieldWithPath("data.content[].contentId").type(JsonFieldType.NUMBER).description("콘텐츠 ID"));
        fieldDescriptors.add(fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("콘텐츠 제목"));
        fieldDescriptors.add(fieldWithPath("data.content[].description").type(JsonFieldType.STRING).description("콘텐츠 설명"));
        fieldDescriptors.add(fieldWithPath("data.content[].thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 URL"));
        fieldDescriptors.add(fieldWithPath("data.content[].createdAt").type(JsonFieldType.STRING).description("북마크 생성 시간"));
        
        return fieldDescriptors.toArray(new FieldDescriptor[0]);
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
     * 유연한 응답 필드 스니펫 생성
     * 문서화되지 않은 필드가 있어도 오류가 발생하지 않음
     */
    public static ResponseFieldsSnippet relaxedResponseFields(FieldDescriptor... descriptors) {
        return org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields(descriptors);
    }
    
    /**
     * 테스트에 사용할 인증된 사용자를 생성합니다.
     */
    public static RequestPostProcessor getTestUser() {
        UserPrincipal userPrincipal = UserPrincipal.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .role("USER")
            .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
            .build();
        
        return SecurityMockMvcRequestPostProcessors.user(userPrincipal);
    }
    
    /**
     * 테스트에 사용할 인증된 관리자를 생성합니다.
     */
    public static RequestPostProcessor getTestAdmin() {
        UserPrincipal adminPrincipal = UserPrincipal.builder()
            .id(2L)
            .username("testadmin")
            .email("admin@example.com")
            .role("ADMIN")
            .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
            .build();
        
        return SecurityMockMvcRequestPostProcessors.user(adminPrincipal);
    }
    
    /**
     * data가 null인 응답에 대한 필드 정의
     */
    public static FieldDescriptor[] getCommonResponseFieldsWithNullData() {
        return new FieldDescriptor[] {
            fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS/ERROR)"),
            fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
            fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)")
        };
    }

    /**
     * 기본 헬스 체크 API 응답 필드를 정의합니다.
     */
    public static FieldDescriptor[] getBasicHealthCheckResponseFields() {
        return new FieldDescriptor[] {
            fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS/ERROR)"),
            fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
            fieldWithPath("data.status").type(JsonFieldType.STRING).description("시스템 상태 (UP/DOWN)"),
            fieldWithPath("data.timestamp").type(JsonFieldType.NUMBER).description("타임스탬프")
        };
    }
    
    /**
     * 상세 헬스 체크 API 응답 필드를 정의합니다.
     */
    public static FieldDescriptor[] getDetailedHealthCheckResponseFields() {
        return new FieldDescriptor[] {
            fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS/ERROR)"),
            fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
            fieldWithPath("data.status").type(JsonFieldType.STRING).description("시스템 상태 (UP/DOWN)"),
            fieldWithPath("data.timestamp").type(JsonFieldType.NUMBER).description("타임스탬프"),
            fieldWithPath("data.details").type(JsonFieldType.OBJECT).description("상세 헬스 정보"),
            subsectionWithPath("data.details.db").type(JsonFieldType.OBJECT).description("데이터베이스 상태 정보").optional(),
            subsectionWithPath("data.details.diskSpace").type(JsonFieldType.OBJECT).description("디스크 공간 상태 정보").optional()
        };
    }
    
    /**
     * 상세 헬스 체크 API의 유연한 응답 필드를 위한 스니펫을 생성합니다.
     * 다양한 컴포넌트와 상세 정보가 동적으로 포함될 수 있어 유연한 문서화가 필요합니다.
     */
    public static ResponseFieldsSnippet getRelaxedHealthCheckResponseFields() {
        return relaxedResponseFields(
            fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS/ERROR)"),
            fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
            fieldWithPath("data.status").type(JsonFieldType.STRING).description("시스템 상태 (UP/DOWN)"),
            fieldWithPath("data.timestamp").type(JsonFieldType.NUMBER).description("타임스탬프"),
            subsectionWithPath("data.details").type(JsonFieldType.OBJECT).description("상세 헬스 정보 (컴포넌트별 상태 포함)")
        );
    }

    /**
     * 상위 사용자 랭킹 목록 응답 필드를 정의합니다.
     */
    public static FieldDescriptor[] getTopRankingResponseFields() {
        return new FieldDescriptor[] {
            fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS/ERROR)"),
            fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
            fieldWithPath("data").type(JsonFieldType.ARRAY).description("상위 사용자 랭킹 목록"),
            fieldWithPath("data[].userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
            fieldWithPath("data[].score").type(JsonFieldType.NUMBER).description("사용자 점수"),
            fieldWithPath("data[].rankType").type(JsonFieldType.STRING).description("랭크 타입 (BRONZE, SILVER, GOLD, PLATINUM, DIAMOND)"),
            fieldWithPath("data[].rankDisplayName").type(JsonFieldType.STRING).description("랭크 표시 이름"),
            fieldWithPath("data[].lastActivityDate").type(JsonFieldType.STRING).description("마지막 활동 일시"),
            fieldWithPath("data[].suspiciousActivity").type(JsonFieldType.BOOLEAN).description("의심스러운 활동 여부"),
            fieldWithPath("data[].reportCount").type(JsonFieldType.NUMBER).description("신고 횟수"),
            fieldWithPath("data[].accountSuspended").type(JsonFieldType.BOOLEAN).description("계정 정지 여부")
        };
    }
    
    /**
     * 랭크 타입별 사용자 목록 페이지 응답 필드를 정의합니다.
     */
    public static FieldDescriptor[] getRankingPageResponseFields() {
        List<FieldDescriptor> fieldDescriptors = new ArrayList<>();
        
        // 기본 응답 필드 추가
        Collections.addAll(fieldDescriptors, getCommonResponseFieldsWithData());
        
        // 페이지네이션 관련 필드 추가
        fieldDescriptors.add(fieldWithPath("data.content").type(JsonFieldType.ARRAY).description("랭크 타입별 사용자 목록"));
        fieldDescriptors.add(fieldWithPath("data.content[].userId").type(JsonFieldType.NUMBER).description("사용자 ID"));
        fieldDescriptors.add(fieldWithPath("data.content[].score").type(JsonFieldType.NUMBER).description("사용자 점수"));
        fieldDescriptors.add(fieldWithPath("data.content[].rankType").type(JsonFieldType.STRING).description("랭크 타입"));
        fieldDescriptors.add(fieldWithPath("data.content[].rankDisplayName").type(JsonFieldType.STRING).description("랭크 표시 이름"));
        fieldDescriptors.add(fieldWithPath("data.content[].lastActivityDate").type(JsonFieldType.STRING).description("마지막 활동 일시"));
        fieldDescriptors.add(fieldWithPath("data.content[].suspiciousActivity").type(JsonFieldType.BOOLEAN).description("의심스러운 활동 여부"));
        fieldDescriptors.add(fieldWithPath("data.content[].reportCount").type(JsonFieldType.NUMBER).description("신고 횟수"));
        fieldDescriptors.add(fieldWithPath("data.content[].accountSuspended").type(JsonFieldType.BOOLEAN).description("계정 정지 여부"));
        
        // 페이징 관련 필드 추가
        fieldDescriptors.add(fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"));
        fieldDescriptors.add(fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"));
        fieldDescriptors.add(fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"));
        fieldDescriptors.add(fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 존재 여부"));
        fieldDescriptors.add(fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"));
        fieldDescriptors.add(fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"));
        fieldDescriptors.add(fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("페이지 오프셋"));
        fieldDescriptors.add(fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"));
        fieldDescriptors.add(fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이징 여부"));
        fieldDescriptors.add(fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 항목 수"));
        fieldDescriptors.add(fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"));
        fieldDescriptors.add(fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"));
        fieldDescriptors.add(fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"));
        fieldDescriptors.add(fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"));
        fieldDescriptors.add(fieldWithPath("data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"));
        fieldDescriptors.add(fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 존재 여부"));
        fieldDescriptors.add(fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"));
        fieldDescriptors.add(fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"));
        fieldDescriptors.add(fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"));
        fieldDescriptors.add(fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 항목 수"));
        fieldDescriptors.add(fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("페이지 결과 존재 여부"));
        
        return fieldDescriptors.toArray(new FieldDescriptor[0]);
    }
    
    /**
     * 랭킹 통계 응답 필드를 정의합니다.
     */
    public static FieldDescriptor[] getRankingStatisticsResponseFields() {
        return new FieldDescriptor[] {
            fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS/ERROR)"),
            fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
            fieldWithPath("data").type(JsonFieldType.OBJECT).description("랭킹 통계 정보"),
            fieldWithPath("data.rankDistribution").type(JsonFieldType.OBJECT).description("랭크 타입별 사용자 분포"),
            fieldWithPath("data.rankDistribution.BRONZE").type(JsonFieldType.NUMBER).description("브론즈 랭크 사용자 수").optional(),
            fieldWithPath("data.rankDistribution.SILVER").type(JsonFieldType.NUMBER).description("실버 랭크 사용자 수").optional(),
            fieldWithPath("data.rankDistribution.GOLD").type(JsonFieldType.NUMBER).description("골드 랭크 사용자 수").optional(),
            fieldWithPath("data.rankDistribution.PLATINUM").type(JsonFieldType.NUMBER).description("플래티넘 랭크 사용자 수").optional(),
            fieldWithPath("data.rankDistribution.DIAMOND").type(JsonFieldType.NUMBER).description("다이아몬드 랭크 사용자 수").optional(),
            fieldWithPath("data.averageScore").type(JsonFieldType.NUMBER).description("전체 사용자의 평균 점수")
        };
    }
    
    /**
     * 랭킹 활동 점수 업데이트 응답 필드를 정의합니다.
     */
    public static FieldDescriptor[] getRankingActivityResponseFields() {
        return new FieldDescriptor[] {
            fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS/ERROR)"),
            fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
            fieldWithPath("data").type(JsonFieldType.OBJECT).description("업데이트된 사용자 랭킹 정보"),
            fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
            fieldWithPath("data.score").type(JsonFieldType.NUMBER).description("사용자 점수"),
            fieldWithPath("data.rankType").type(JsonFieldType.STRING).description("랭크 타입"),
            fieldWithPath("data.rankDisplayName").type(JsonFieldType.STRING).description("랭크 표시 이름"),
            fieldWithPath("data.lastActivityDate").type(JsonFieldType.STRING).description("마지막 활동 일시"),
            fieldWithPath("data.suspiciousActivity").type(JsonFieldType.BOOLEAN).description("의심스러운 활동 여부"),
            fieldWithPath("data.reportCount").type(JsonFieldType.NUMBER).description("신고 횟수"),
            fieldWithPath("data.accountSuspended").type(JsonFieldType.BOOLEAN).description("계정 정지 여부")
        };
    }
    
    /**
     * 시스템 설정 응답 필드를 정의합니다.
     */
    public static FieldDescriptor[] getSystemSettingResponseFields() {
        return new FieldDescriptor[] {
            fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS/ERROR)"),
            fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
            fieldWithPath("data").type(JsonFieldType.OBJECT).description("시스템 설정 정보"),
            fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("설정 ID"),
            fieldWithPath("data.key").type(JsonFieldType.STRING).description("설정 키"),
            fieldWithPath("data.value").type(JsonFieldType.STRING).description("설정 값"),
            fieldWithPath("data.description").type(JsonFieldType.STRING).description("설정 설명"),
            fieldWithPath("data.category").type(JsonFieldType.STRING).description("설정 카테고리"),
            fieldWithPath("data.encrypted").type(JsonFieldType.BOOLEAN).description("암호화 여부"),
            fieldWithPath("data.systemManaged").type(JsonFieldType.BOOLEAN).description("시스템 관리 여부"),
            fieldWithPath("data.lastModifiedBy").type(JsonFieldType.NUMBER).description("마지막 수정자 ID"),
            fieldWithPath("data.defaultValue").type(JsonFieldType.STRING).description("기본값").optional(),
            fieldWithPath("data.validationPattern").type(JsonFieldType.STRING).description("유효성 검사 패턴").optional()
        };
    }

    /**
     * 시스템 설정 목록 응답 필드를 정의합니다.
     */
    public static FieldDescriptor[] getSystemSettingListResponseFields() {
        return new FieldDescriptor[] {
            fieldWithPath("result").type(JsonFieldType.STRING).description("결과 상태 (SUCCESS/ERROR)"),
            fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
            fieldWithPath("data").type(JsonFieldType.ARRAY).description("시스템 설정 목록"),
            fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("설정 ID"),
            fieldWithPath("data[].key").type(JsonFieldType.STRING).description("설정 키"),
            fieldWithPath("data[].value").type(JsonFieldType.STRING).description("설정 값"),
            fieldWithPath("data[].description").type(JsonFieldType.STRING).description("설정 설명"),
            fieldWithPath("data[].category").type(JsonFieldType.STRING).description("설정 카테고리"),
            fieldWithPath("data[].encrypted").type(JsonFieldType.BOOLEAN).description("암호화 여부"),
            fieldWithPath("data[].systemManaged").type(JsonFieldType.BOOLEAN).description("시스템 관리 여부"),
            fieldWithPath("data[].lastModifiedBy").type(JsonFieldType.NUMBER).description("마지막 수정자 ID"),
            fieldWithPath("data[].defaultValue").type(JsonFieldType.STRING).description("기본값").optional(),
            fieldWithPath("data[].validationPattern").type(JsonFieldType.STRING).description("유효성 검사 패턴").optional()
        };
    }
} 