package pokemon.pokedex.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UsernameValidator implements ConstraintValidator<Username, String> {
    private static final Pattern KOREAN_ONLY = Pattern.compile("^[가-힣]+$");
    private static final Pattern ENGLISH_ONLY = Pattern.compile("^[a-zA-Z]+$");
    private static final Pattern KOREAN_NUMERIC = Pattern.compile("^(?=.*[가-힣])[가-힣0-9]+$");
    private static final Pattern ENGLISH_NUMERIC = Pattern.compile("^(?=.*[a-zA-Z])[a-zA-Z0-9]+$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;

        // 한글만 or 영어만 or 한글+숫자 or 영어+숫자
        return KOREAN_ONLY.matcher(value).matches()
                || ENGLISH_ONLY.matcher(value).matches()
                || KOREAN_NUMERIC.matcher(value).matches()
                || ENGLISH_NUMERIC.matcher(value).matches();
    }
}

