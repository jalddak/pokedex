package pokemon.pokedex.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordComplexityValidator implements ConstraintValidator<PasswordComplexity, String> {

    private static final Pattern LETTER = Pattern.compile("[A-Za-z]");
    private static final Pattern DIGIT = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL = Pattern.compile("[^A-Za-z0-9]");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;

        int count = 0;
        if (LETTER.matcher(value).find()) count++;
        if (DIGIT.matcher(value).find()) count++;
        if (SPECIAL.matcher(value).find()) count++;

        // 영문, 숫자, 특수문자 중 최소 2가지 이상 혼합
        return count >= 2;
    }
}

