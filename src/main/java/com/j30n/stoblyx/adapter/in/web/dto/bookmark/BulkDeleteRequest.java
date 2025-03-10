package com.j30n.stoblyx.adapter.in.web.dto.bookmark;

import java.util.List;

public record BulkDeleteRequest(
    List<Long> contentIds
) {
    public List<Long> getContentIds() {
        return contentIds;
    }
} 