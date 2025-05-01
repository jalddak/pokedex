package pokemon.pokedex.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pokemon.pokedex.user.domain.User;
import pokemon.pokedex.user.dto.CheckedUserDTO;
import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.exception.LoginFailedException;
import pokemon.pokedex.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoginServiceImpl loginServiceImpl;

    @Test
    @DisplayName("로그인 성공")
    void checkLogin_success() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setLoginId("testLoginId");
        loginDTO.setPassword("testPassword");

        User user = new User();
        user.setLoginId(loginDTO.getLoginId());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(loginDTO.getPassword());
        user.setPassword(encodedPassword);
        doReturn(Optional.of(user)).when(userRepository).findByLoginId(any(String.class));

        CheckedUserDTO checkedUserDTO = loginServiceImpl.checkLogin(loginDTO);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(userRepository, times(1)).findByLoginId(captor.capture());
        String usedLoginId = captor.getValue();
        assertThat(usedLoginId).isEqualTo(loginDTO.getLoginId());
        assertThat(checkedUserDTO.getLoginId()).isEqualTo(user.getLoginId());
    }

    @Test
    @DisplayName("로그인 실패 예외")
    void checkLogin_fail_exception() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setLoginId("testLoginId");
        loginDTO.setPassword("testPassword");

        User user = new User();
        user.setLoginId(loginDTO.getLoginId());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode("anotherPassword");
        user.setPassword(encodedPassword);
        doReturn(Optional.of(user)).when(userRepository).findByLoginId(any(String.class));

        assertThatThrownBy(() -> loginServiceImpl.checkLogin(loginDTO))
                .isInstanceOf(LoginFailedException.class);

    }

}