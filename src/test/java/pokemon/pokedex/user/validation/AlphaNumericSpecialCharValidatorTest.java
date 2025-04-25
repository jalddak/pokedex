package pokemon.pokedex.user.validation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AlphaNumericSpecialCharValidatorTest {

    private AlphaNumericSpecialCharValidator validator = new AlphaNumericSpecialCharValidator();

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\n", "\t", "     "})
    void testNullOrBlank(String input) {
        assertThat(validator.isValid(input, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Valid123", "HelloWorld", "test123", "#asdf123", "!@!@$", "123123", "ASdfja"})
    void testValidString(String input) {
        assertThat(validator.isValid(input, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Bad Input!", "한글", "asd    asd", "asd\nasd", "a\n"})
    void testInValidString(String input) {
        assertThat(validator.isValid(input, null)).isFalse();
    }
}