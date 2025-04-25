package pokemon.pokedex.user.validation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UsernameValidatorTest {

    private UsernameValidator validator = new UsernameValidator();

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\n", "\t", "     "})
    void testNullOrBlank(String input) {
        assertThat(validator.isValid(input, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"오박사", "오박사123", "asdf123", "asdf"})
    void testValidString(String input) {
        assertThat(validator.isValid(input, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"오 박사", "오_박사", "ㄴㅇㄹ", "oh박사",
            "as\nlkd", "asd\tf", "asdf ", "asdf\n", "!@#$!@%!@$", "123"})
    void testInValidString(String input) {
        assertThat(validator.isValid(input, null)).isFalse();
    }

}