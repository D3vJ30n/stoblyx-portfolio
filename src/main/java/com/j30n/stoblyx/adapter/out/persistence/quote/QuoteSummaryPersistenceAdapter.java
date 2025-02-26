package com.j30n.stoblyx.adapter.out.persistence.quote;

import com.j30n.stoblyx.application.port.out.quote.QuoteSummaryPort;
import com.j30n.stoblyx.infrastructure.client.KoBartClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuoteSummaryPersistenceAdapter implements QuoteSummaryPort {

    private final KoBartClient koBartClient;

    @Override
    public String summarize(String content) {
        return koBartClient.summarize(content);
    }
}
