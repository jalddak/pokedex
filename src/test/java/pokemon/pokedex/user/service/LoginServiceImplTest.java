package pokemon.pokedex.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pokemon.pokedex.user.domain.User;
import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.exception.LoginFailedException;
import pokemon.pokedex.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static pokemon.pokedex.__testutils.TestDataFactory.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoginServiceImpl loginServiceImpl;

    @Test
    @DisplayName("로그인 성공")
    void checkLogin_success() {
        LoginDTO loginDTO = createLoginDTO(defaultInfos);
        User user = createUser(defaultInfos);
        doReturn(Optional.of(user)).when(userRepository).findByLoginId(any(String.class));

        SessionUserDTO sessionUserDTO = loginServiceImpl.checkLogin(loginDTO);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(userRepository, times(1)).findByLoginId(captor.capture());
        String usedLoginId = captor.getValue();
        assertThat(usedLoginId).isEqualTo(loginDTO.getLoginId());
        assertThat(sessionUserDTO.getLoginId()).isEqualTo(user.getLoginId());
    }

    @Test
    @DisplayName("로그인 실패 예외 - 패스워드 불일치")
    void checkLogin_fail_exception_password_wrong() {
        LoginDTO loginDTO = createLoginDTO(defaultInfos);
        loginDTO.setPassword("wrongPassword123");
        doReturn(Optional.of(createUser(defaultInfos))).when(userRepository).findByLoginId(any(String.class));

        assertThatThrownBy(() -> loginServiceImpl.checkLogin(loginDTO))
                .isInstanceOf(LoginFailedException.class);

    }

    @Test
    @DisplayName("로그인 실패 예외 - 이미 삭제된 유저")
    void checkLogin_fail_exception_deleted_user() {
        LoginDTO loginDTO = createLoginDTO(defaultInfos);
        User deltedUser = createUser(defaultInfos);
        deltedUser.setIsDeleted(true);
        deltedUser.setDeletedAt(LocalDateTime.now());
        doReturn(Optional.of(deltedUser)).when(userRepository).findByLoginId(any(String.class));

        assertThatThrownBy(() -> loginServiceImpl.checkLogin(loginDTO))
                .isInstanceOf(LoginFailedException.class);

    }

}