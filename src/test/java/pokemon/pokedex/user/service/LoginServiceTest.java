package pokemon.pokedex.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pokemon.pokedex.ClearMemory;
import pokemon.pokedex.user.domain.User;
import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.exception.LoginFailedException;
import pokemon.pokedex.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class LoginServiceTest extends ClearMemory {

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserRepository userRepository;

    private LoginDTO loginDTO;

    @BeforeEach
    void setUp() {
        loginDTO = new LoginDTO();
        loginDTO.setLoginId("testLoginId");
        loginDTO.setPassword("testPassword");
    }

    @Test
    @DisplayName("로그인 성공")
    void checkLogin_success() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setLoginId(loginDTO.getLoginId());
        user.setPassword(encoder.encode(loginDTO.getPassword()));
        user.setIsDeleted(false);
        userRepository.save(user);

        SessionUserDTO sessionUserDTO = loginService.checkLogin(loginDTO);
        assertThat(sessionUserDTO.getLoginId()).isEqualTo(loginDTO.getLoginId());
    }

    @Test
    @DisplayName("로그인 실패 예외처리 - 패스워드 불일치")
    void checkLogin_fail_exception_password_wrong() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setLoginId(loginDTO.getLoginId());
        user.setPassword(encoder.encode("anotherPassword"));
        user.setIsDeleted(false);
        userRepository.save(user);

        assertThatThrownBy(() -> loginService.checkLogin(loginDTO))
                .isInstanceOf(LoginFailedException.class);
    }

    @Test
    @DisplayName("로그인 실패 예외처리 - 이미 삭제된 유저")
    void checkLogin_fail_exception_deleted_user() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setLoginId(loginDTO.getLoginId());
        user.setPassword(encoder.encode(loginDTO.getPassword()));
        user.setIsDeleted(true);
        userRepository.save(user);

        assertThatThrownBy(() -> loginService.checkLogin(loginDTO))
                .isInstanceOf(LoginFailedException.class);
    }
}