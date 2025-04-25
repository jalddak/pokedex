package pokemon.pokedex.user.validation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AlphaNumericValidatorTest {

    private AlphaNumericValidator validator = new AlphaNumericValidator();

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\n", "\t", "     "})
    void testNullOrBlank(String input) {
        assertThat(validator.isValid(input, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"asd123", "123asd", "123asd123", "asfasdf"})
    void testValidString(String input) {
        assertThat(validator.isValid(input, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"한글", "ㅎㄴㅇㄹ", "asd 123", "asd\nasd", "123123", "a\n"})
    void testInValidString(String input) {
        assertThat(validator.isValid(input, null)).isFalse();
    }

}