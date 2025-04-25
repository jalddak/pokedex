package pokemon.pokedex.user.validation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PasswordComplexityValidatorTest {

    private PasswordComplexityValidator validator = new PasswordComplexityValidator();

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\n", "\t", "     "})
    void testNullOrBlank(String input) {
        assertThat(validator.isValid(input, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"asd123", "asd!@#", "123!@#", "asd123!@#"})
    void testValidString(String input) {
        assertThat(validator.isValid(input, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "!@#", "asd"})
    void testInValidString(String input) {
        assertThat(validator.isValid(input, null)).isFalse();
    }
}