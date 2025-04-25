package pokemon.pokedex.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AlphaNumericSpecialCharValidator.class)
public @interface AlphaNumericSpecialCharOnly {
    String message() default "영어, 숫자, 특수문자만 허용됩니다.";  // 기본 오류 메시지

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
