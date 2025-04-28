package pokemon.pokedex.user.dto;

import lombok.Getter;
import lombok.Setter;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.domain.Role;
import pokemon.pokedex.user.domain.User;

@Getter
@Setter
public class LoginResponseDTO {

    private Long id;
    private String loginId;
    private String username;
    private Role role;
    private AdminRequestStatus adminRequestStatus;

    public LoginResponseDTO() {
    }

    private LoginResponseDTO(User user) {
        this.id = user.getId();
        this.loginId = user.getLoginId();
        this.username = user.getUsername();
        this.role = user.getRole();
        this.adminRequestStatus = user.getAdminRequestStatus();
    }

    public static LoginResponseDTO createByUser(User user) {
        return new LoginResponseDTO(user);
    }
}
