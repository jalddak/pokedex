package pokemon.pokedex.user.dto;

import lombok.Getter;
import lombok.Setter;
import pokemon.pokedex.user.domain.User;


@Getter
@Setter
public class RegisterResponseDTO {

    private Long id;
    private String username;

    public RegisterResponseDTO() {
    }

    private RegisterResponseDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }


    public static RegisterResponseDTO createByUser(User user) {
        return new RegisterResponseDTO(user);
    }
}
