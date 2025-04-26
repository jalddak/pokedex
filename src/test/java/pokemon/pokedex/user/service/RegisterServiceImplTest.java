package pokemon.pokedex.user.service;

import lombok.extern.slf4j.Slf4j;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
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
        registerDTO.setPassword("testPassword");
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
    @DisplayName("같은 비밀번호를 입력해도 다른 값이 나오는지 확인")
    void encodedPassword() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setPassword("test1234");
        User user = User.createByRegisterDto(registerDTO);

        doReturn(user).when(userRepository).save(any(User.class));

        registerServiceImpl.addUser(registerDTO);
        registerServiceImpl.addUser(registerDTO);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(2)).save(captor.capture());

        List<User> allValues = captor.getAllValues();
        User usedUser1 = allValues.get(0);
        User usedUser2 = allValues.get(1);

        assertThat(usedUser1.getPassword()).isNotEqualTo(registerDTO.getPassword());
        assertThat(usedUser2.getPassword()).isNotEqualTo(usedUser1.getPassword());

        log.info("registerDTO password: {}", registerDTO.getPassword());
        log.info("user1 password: {}", usedUser1.getPassword());
        log.info("user2 password: {}", usedUser2.getPassword());
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