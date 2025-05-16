package pokemon.pokedex.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pokemon.pokedex.__testutils.ClearMemory;
import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.exception.LoginFailedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pokemon.pokedex.__testutils.TestDataFactory.*;

@SpringBootTest
class LoginServiceTest {

    @Autowired
    private ClearMemory clearMemory;

    @Autowired
    private LoginService loginService;

    private LoginDTO loginDTO;

    @BeforeEach
    void setUp() {
        clearMemory.clearMemory();
        loginDTO = createLoginDTO(defaultInfos);
    }

    @Test
    @DisplayName("로그인 성공")
    void checkLogin_success() {
        SessionUserDTO sessionUserDTO = loginService.checkLogin(loginDTO);
        assertThat(sessionUserDTO.getLoginId()).isEqualTo(loginDTO.getLoginId());
    }

    @Test
    @DisplayName("로그인 실패 예외처리 - 패스워드 불일치")
    void checkLogin_fail_exception_password_wrong() {
        loginDTO.setPassword("wrongPassword123");
        assertThatThrownBy(() -> loginService.checkLogin(loginDTO))
                .isInstanceOf(LoginFailedException.class);
    }

    @Test
    @DisplayName("로그인 실패 예외처리 - 이미 삭제된 유저")
    void checkLogin_fail_exception_deleted_user() {
        loginDTO = createLoginDTO(deletedInfos);
        assertThatThrownBy(() -> loginService.checkLogin(loginDTO))
                .isInstanceOf(LoginFailedException.class);
    }
}