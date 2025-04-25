package pokemon.pokedex.user.domain;

import lombok.Getter;
import lombok.Setter;
import pokemon.pokedex.user.dto.RegisterDTO;

import java.time.LocalDateTime;

@Getter
@Setter
public class User {
    private Long id;
    private String loginId;
    private String password;
    private String email;
    private String username;

    private Role role;
    private Boolean isDeleted;
    private AdminRequestStatus adminRequestStatus;
    private Long approvedByAdminId;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public User() {
    }

    private User(RegisterDTO registerDTO) {
        this.id = null;
        this.loginId = registerDTO.getLoginId();
        this.password = registerDTO.getPassword();
        this.email = registerDTO.getEmail();
        this.username = registerDTO.getUsername();

        this.role = Role.NORMAL;
        this.adminRequestStatus = AdminRequestStatus.NONE;
        this.approvedByAdminId = null;
        this.createdAt = null;
        this.isDeleted = false;
        this.deletedAt = null;
    }

    public static User createByRegisterDto(RegisterDTO registerDTO) {
        return new User(registerDTO);
    }
}
