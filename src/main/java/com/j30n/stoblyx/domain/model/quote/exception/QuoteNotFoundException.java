package com.j30n.stoblyx.domain.model.quote.exception;

import com.j30n.stoblyx.common.exception.DomainException;
import com.j30n.stoblyx.common.exception.EntityAlreadyExistsException;
import com.j30n.stoblyx.common.exception.EntityNotFoundException;
import com.j30n.stoblyx.common.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import com.j30n.stoblyx.domain.model.quote.QuoteId;

/**
 * 인용구를 찾을 수 없을 때 발생하는 예외
 */
public class QuoteNotFoundException extends EntityNotFoundException {
    public QuoteNotFoundException(QuoteId quoteId) {
        super("인용구를 찾을 수 없습니다: " + quoteId.getValue());
    }

    public QuoteNotFoundException(String message) {
        super(message);
    }

    public QuoteNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}