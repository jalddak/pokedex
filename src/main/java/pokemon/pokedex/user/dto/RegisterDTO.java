package pokemon.pokedex.user.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import pokemon.pokedex.user.validation.AlphaNumeric;
import pokemon.pokedex.user.validation.AlphaNumericSpecialCharOnly;
import pokemon.pokedex.user.validation.PasswordComplexity;
import pokemon.pokedex.user.validation.Username;

@Getter
@Setter
public class RegisterDTO {

    @NotBlank
    @Size(min = 2, max = 12)
    @Username
    private String username;

    @NotBlank
    @Size(min = 3, max = 25)
    @AlphaNumeric
    private String loginId;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 32)
    @AlphaNumericSpecialCharOnly
    @PasswordComplexity
    private String password;

    @NotBlank
    private String confirmPassword;

    @AssertTrue(message = "Passwords don't match")
    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }
}
