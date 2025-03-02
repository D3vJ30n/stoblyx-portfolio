package com.j30n.stoblyx.adapter.in.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 관리자 대시보드 페이지를 처리하는 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminDashboardController {

    /**
     * 관리자 대시보드 메인 페이지를 반환합니다.
     *
     * @return 대시보드 뷰 이름
     */
    @GetMapping
    public String dashboard() {
        return "admin/dashboard";
    }

    /**
     * 사용자 관리 페이지를 반환합니다.
     *
     * @return 사용자 관리 뷰 이름
     */
    @GetMapping("/users")
    public String userManagement() {
        return "admin/users";
    }

    /**
     * 책 관리 페이지를 반환합니다.
     *
     * @return 책 관리 뷰 이름
     */
    @GetMapping("/books")
    public String bookManagement() {
        return "admin/books";
    }
} 