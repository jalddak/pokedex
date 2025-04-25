package pokemon.pokedex.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AlphaNumericValidator.class)  // 유효성 검사기 지정
public @interface AlphaNumeric {
    String message() default "영문, 영문 + 숫자만 허용됩니다.";  // 기본 메시지

    Class<?>[] groups() default {};  // 그룹 설정 (그룹화할 때 사용)

    Class<? extends Payload>[] payload() default {};  // 부가적인 정보
}
