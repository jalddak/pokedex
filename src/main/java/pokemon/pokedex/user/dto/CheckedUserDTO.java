package pokemon.pokedex.user.dto;

import lombok.Getter;
import lombok.Setter;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.domain.Role;
import pokemon.pokedex.user.domain.User;

@Getter
@Setter
public class CheckedUserDTO {

    private Long id;
    private String loginId;
    private String username;
    private Role role;
    private AdminRequestStatus adminRequestStatus;

    public CheckedUserDTO() {
    }

    private CheckedUserDTO(User user) {
        this.id = user.getId();
        this.loginId = user.getLoginId();
        this.username = user.getUsername();
        this.role = user.getRole();
        this.adminRequestStatus = user.getAdminRequestStatus();
    }

    public static CheckedUserDTO createByUser(User user) {
        return new CheckedUserDTO(user);
    }
}
