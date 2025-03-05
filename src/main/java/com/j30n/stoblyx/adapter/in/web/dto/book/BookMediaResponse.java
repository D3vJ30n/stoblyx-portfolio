package com.j30n.stoblyx.adapter.in.web.dto.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 책 멀티미디어 DTO
 * 책에 대한 다양한 멀티미디어 자원의 경로를 담습니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookMediaResponse {
    private String imageUrl;
    private String videoUrl;
    private String audioUrl;
    private String bgmUrl;
} 