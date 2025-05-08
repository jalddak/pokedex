package pokemon.pokedex.user.dto;

import lombok.Getter;
import lombok.Setter;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.domain.Role;
import pokemon.pokedex.user.domain.User;

@Getter
@Setter
public class SessionUserDTO {

    private Long id;
    private String loginId;
    private String username;
    private Role role;
    private AdminRequestStatus adminRequestStatus;

    public SessionUserDTO() {
    }

    private SessionUserDTO(User user) {
        this.id = user.getId();
        this.loginId = user.getLoginId();
        this.username = user.getUsername();
        this.role = user.getRole();
        this.adminRequestStatus = user.getAdminRequestStatus();
    }

    public static SessionUserDTO createByUser(User user) {
        return new SessionUserDTO(user);
    }
}
