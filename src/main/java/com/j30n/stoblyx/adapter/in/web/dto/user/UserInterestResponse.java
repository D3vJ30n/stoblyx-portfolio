package com.j30n.stoblyx.adapter.in.web.dto.user;

import com.j30n.stoblyx.domain.model.UserInterest;
import java.util.List;

public record UserInterestResponse(
    Long id,
    Long userId,
    List<String> genres,
    List<String> authors,
    List<String> keywords
) {
    public static UserInterestResponse from(UserInterest userInterest) {
        return new UserInterestResponse(
            userInterest.getId(),
            userInterest.getUser().getId(),
            userInterest.getGenres(),
            userInterest.getAuthors(),
            userInterest.getKeywords()
        );
    }
}