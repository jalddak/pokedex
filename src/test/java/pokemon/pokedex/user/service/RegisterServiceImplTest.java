package pokemon.pokedex.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pokemon.pokedex.user.domain.User;
import pokemon.pokedex.user.dto.RegisterDTO;
import pokemon.pokedex.user.dto.RegisterResponseDTO;
import pokemon.pokedex.user.exception.DuplicateLoginIdException;
import pokemon.pokedex.user.repository.UserRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegisterServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RegisterServiceImpl registerServiceImpl;

    @Test
    @DisplayName("유저 추가")
    void addUser() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setLoginId("user");
        registerDTO.setUsername("testUsername");
        User user = User.createByRegisterDto(registerDTO);
        doReturn(user).when(userRepository).save(any(User.class));

        RegisterResponseDTO registerResponseDTO = registerServiceImpl.addUser(registerDTO);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User usedUser = captor.getValue();
        assertThat(usedUser.getLoginId()).isEqualTo(user.getLoginId());
        assertThat(registerResponseDTO.getUsername()).isEqualTo(registerDTO.getUsername());
    }

    @Test
    @DisplayName("중복 아이디 아니면 정상작동")
    void validateDuplicatedLoginId_normal() {
        String loginId = UUID.randomUUID().toString();
        doReturn(false).when(userRepository).existsByLoginId(any(String.class));

        registerServiceImpl.validateDuplicatedLoginId(loginId);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(userRepository).existsByLoginId(captor.capture());

        String usedLoginId = captor.getValue();
        assertThat(usedLoginId).isEqualTo(loginId);
    }

    @Test
    @DisplayName("중복 아이디면 예외발생")
    void validateDuplicatedLoginId_exception() {
        String loginId = UUID.randomUUID().toString();
        doReturn(true).when(userRepository).existsByLoginId(any(String.class));

        assertThatThrownBy(() -> registerServiceImpl.validateDuplicatedLoginId(loginId))
                .isInstanceOf(DuplicateLoginIdException.class);
    }
}