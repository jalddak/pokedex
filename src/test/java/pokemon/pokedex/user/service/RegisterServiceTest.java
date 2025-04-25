package pokemon.pokedex.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pokemon.pokedex.user.dto.RegisterDTO;
import pokemon.pokedex.user.dto.RegisterResponseDTO;
import pokemon.pokedex.user.exception.DuplicateLoginIdException;
import pokemon.pokedex.user.repository.MemoryUserRepository;
import pokemon.pokedex.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class RegisterServiceTest {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private UserRepository userRepository;

    private RegisterDTO registerDTO;

    @BeforeEach
    void setUp() {
        if (userRepository instanceof MemoryUserRepository) {
            ((MemoryUserRepository) userRepository).clear();
        }

        registerDTO = new RegisterDTO();
        registerDTO.setLoginId("testLoginId");
        registerDTO.setUsername("testUsername");
        registerDTO.setEmail("testEmail@email.com");
        registerDTO.setPassword("testPassword123");
        registerDTO.setConfirmPassword("testPassword123");
    }

    @Test
    @DisplayName("유저 추가")
    void addUser() {
        RegisterResponseDTO registerResponseDTO = registerService.addUser(registerDTO);

        assertThat(registerResponseDTO.getUsername()).isEqualTo(registerDTO.getUsername());
    }

    @Test
    @DisplayName("중복 아이디 아니면 정상작동")
    void validateDuplicatedLoginId_normal() {
        registerService.validateDuplicatedLoginId(registerDTO.getLoginId());
    }

    @Test
    @DisplayName("중복 아이디면 예외발생")
    void validateDuplicatedLoginId_exception() {
        registerService.addUser(registerDTO);

        assertThatThrownBy(() -> registerService.validateDuplicatedLoginId(registerDTO.getLoginId()))
                .isInstanceOf(DuplicateLoginIdException.class);
    }
}
