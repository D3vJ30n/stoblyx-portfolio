package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.search.SearchRequest;
import com.j30n.stoblyx.adapter.in.web.dto.search.SearchResponse;
import com.j30n.stoblyx.application.port.in.search.SearchUseCase;
import com.j30n.stoblyx.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 검색 관련 API를 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchUseCase searchUseCase;

    /**
     * 통합 검색 API
     * 문구와 책을 동시에 검색할 수 있습니다.
     *
     * @param request  검색 요청 DTO
     * @param pageable 페이징 정보
     * @return 검색 결과
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<SearchResponse>>> search(
        @Valid @ModelAttribute SearchRequest request,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(
            new ApiResponse<>("SUCCESS", "검색 결과입니다.",
                searchUseCase.search(request, pageable))
        );
    }
}
