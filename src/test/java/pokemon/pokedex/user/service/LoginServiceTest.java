package pokemon.pokedex.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pokemon.pokedex.user.domain.User;
import pokemon.pokedex.user.dto.CheckedUserDTO;
import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.exception.LoginFailedException;
import pokemon.pokedex.user.repository.MemoryUserRepository;
import pokemon.pokedex.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class LoginServiceTest {

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserRepository userRepository;

    private LoginDTO loginDTO;

    @BeforeEach
    void setUp() {
        if (userRepository instanceof MemoryUserRepository) {
            ((MemoryUserRepository) userRepository).clear();
        }
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
        userRepository.save(user);

        CheckedUserDTO checkedUserDTO = loginService.checkLogin(loginDTO);
        assertThat(checkedUserDTO.getLoginId()).isEqualTo(loginDTO.getLoginId());
    }

    @Test
    @DisplayName("로그인 실패 예외처리")
    void checkLogin_fail_exception() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setLoginId(loginDTO.getLoginId());
        user.setPassword(encoder.encode("anotherPassword"));
        userRepository.save(user);

        assertThatThrownBy(() -> loginService.checkLogin(loginDTO))
                .isInstanceOf(LoginFailedException.class);
    }
}