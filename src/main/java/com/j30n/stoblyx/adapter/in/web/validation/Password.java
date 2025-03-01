package com.j30n.stoblyx.adapter.in.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "비밀번호는 8자 이상이어야 하며, 영문자, 숫자, 특수문자를 포함해야 합니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
