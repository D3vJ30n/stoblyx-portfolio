package com.j30n.stoblyx.application.service.user;

import com.j30n.stoblyx.adapter.in.web.dto.user.UserInterestRequest;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserInterestResponse;
import com.j30n.stoblyx.application.port.in.user.UserInterestUseCase;
import com.j30n.stoblyx.application.port.out.user.UserInterestPort;
import com.j30n.stoblyx.application.port.out.user.UserPort;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.model.UserInterest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInterestService implements UserInterestUseCase {

    private final UserPort userPort;
    private final UserInterestPort userInterestPort;

    @Override
    @Transactional(readOnly = true)
    public UserInterestResponse getUserInterest(Long userId) {
        log.debug("사용자 관심사 조회: userId={}", userId);
        
        User user = userPort.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        UserInterest userInterest = userInterestPort.findByUserId(userId)
            .orElse(UserInterest.createEmpty(user));

        return UserInterestResponse.from(userInterest);
    }

    @Override
    @Transactional
    public UserInterestResponse updateUserInterest(Long userId, UserInterestRequest request) {
        log.debug("사용자 관심사 수정: userId={}, request={}", userId, request);
        
        User user = userPort.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        UserInterest userInterest = userInterestPort.findByUserId(userId)
            .orElse(UserInterest.createEmpty(user));

        // 관심사 정보를 JSON 문자열로 변환하여 저장
        String interestsJson = convertToJson(request);
        userInterest.updateInterests(interestsJson);
        userInterestPort.save(userInterest);

        return UserInterestResponse.from(userInterest);
    }
    
    /**
     * UserInterestRequest 객체를 JSON 문자열로 변환합니다.
     * 
     * @param request 사용자 관심사 요청 객체
     * @return JSON 형식의 문자열
     */
    private String convertToJson(UserInterestRequest request) {
        // 간단한 JSON 형식으로 변환
        return String.format(
            "{\"genres\":%s,\"authors\":%s,\"keywords\":%s}",
            listToJsonArray(request.genres()),
            listToJsonArray(request.authors()),
            listToJsonArray(request.keywords())
        );
    }
    
    /**
     * 문자열 리스트를 JSON 배열 형식의 문자열로 변환합니다.
     * 
     * @param list 문자열 리스트
     * @return JSON 배열 형식의 문자열
     */
    private String listToJsonArray(java.util.List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append("\"").append(list.get(i).replace("\"", "\\\"")).append("\"");
            if (i < list.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}