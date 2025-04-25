package pokemon.pokedex.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class AlphaNumericSpecialCharValidator implements ConstraintValidator<AlphaNumericSpecialCharOnly, String> {

    //알파벳, 숫자, 특수문자만 허용
    private static final Pattern VALID_PATTERN =
            Pattern.compile("^[a-zA-Z0-9!\"#$%&'()*+,-./:;<=>?@\\[\\]^_`{|}~]*$");

    @Override
    public void initialize(AlphaNumericSpecialCharOnly constraintAnnotation) {
        // 초기화 작업이 필요할 경우 추가
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;

        return VALID_PATTERN.matcher(value).matches();
    }
}
