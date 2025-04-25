package pokemon.pokedex.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class AlphaNumericValidator implements ConstraintValidator<AlphaNumeric, String> {

    //알파벳은 최소 1자이상, 숫자만은 불가능
    private static final Pattern ALPHA_NUMERIC_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])[a-zA-Z0-9]+$");

    @Override
    public void initialize(AlphaNumeric constraintAnnotation) {
        // 초기화 로직 (필요하면)
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;  // null, 공백 값은 따로 처리.

        return ALPHA_NUMERIC_PATTERN.matcher(value).matches();  // 값이 알파벳과 숫자만 포함하는지 체크
    }
}

