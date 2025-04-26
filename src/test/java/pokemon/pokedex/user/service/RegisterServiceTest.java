package pokemon.pokedex.user.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pokemon.pokedex.user.domain.User;
import pokemon.pokedex.user.dto.RegisterDTO;
import pokemon.pokedex.user.dto.RegisterResponseDTO;
import pokemon.pokedex.user.exception.DuplicateLoginIdException;
import pokemon.pokedex.user.repository.MemoryUserRepository;
import pokemon.pokedex.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
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
    @DisplayName("같은 비밀번호를 입력해도 다른 값이 나오는지 확인")
    void encodedPassword() {
        registerDTO.setPassword("test1234");

        RegisterDTO samePasswordDTO = new RegisterDTO();
        samePasswordDTO.setLoginId("samePasswordLoginId");
        samePasswordDTO.setPassword(registerDTO.getPassword());

        registerService.addUser(registerDTO);
        registerService.addUser(samePasswordDTO);

        User user = userRepository.findByLoginId(registerDTO.getLoginId()).get();
        User samePasswordUser = userRepository.findByLoginId(samePasswordDTO.getLoginId()).get();

        assertThat(user.getPassword()).isNotEqualTo(registerDTO.getPassword());
        assertThat(samePasswordUser.getPassword()).isNotEqualTo(user.getPassword());

        log.info("registerDTO password: {}", registerDTO.getPassword());
        log.info("user password: {}", user.getPassword());
        log.info("samePasswordUser password: {}", samePasswordUser.getPassword());
    }

    @Test
    @DisplayName("중복 아이디면 예외발생")
    void validateDuplicatedLoginId_exception() {
        registerService.addUser(registerDTO);

        assertThatThrownBy(() -> registerService.validateDuplicatedLoginId(registerDTO.getLoginId()))
                .isInstanceOf(DuplicateLoginIdException.class);
    }
}
