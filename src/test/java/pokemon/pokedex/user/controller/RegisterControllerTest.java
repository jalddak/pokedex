package pokemon.pokedex.user.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pokemon.pokedex.ClearMemory;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.dto.RegisterDTO;
import pokemon.pokedex.user.dto.RegisterResponseDTO;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.repository.UserRepository;
import pokemon.pokedex.user.service.RegisterService;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RegisterControllerTest extends ClearMemory {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RegisterService registerService;

    @Autowired
    private UserRepository userRepository;

    private RegisterDTO registerDTO;

    private static Stream<Arguments> provideErrorInputs() {
        return Stream.of(
                Arguments.of("username", null, "NotBlank"),
                Arguments.of("username", "a", "Size"),
                Arguments.of("username", "a123456789012", "Size"),
                Arguments.of("username", "!@#!@$!@", "Username"),

                Arguments.of("loginId", null, "NotBlank"),
                Arguments.of("loginId", "          ", "NotBlank"),
                Arguments.of("loginId", "a1", "Size"),
                Arguments.of("loginId", "a1234567890123456789012345", "Size"),
                Arguments.of("loginId", "!@#!@$!@", "AlphaNumeric"),


                Arguments.of("email", null, "NotBlank"),
                Arguments.of("email", "asdf!@#!@", "Email"),


                Arguments.of("password", null, "NotBlank"),
                Arguments.of("password", "          ", "NotBlank"),
                Arguments.of("password", "a123456", "Size"),
                Arguments.of("password", "a12345678901234567890123456789012", "Size"),
                Arguments.of("password", "ㅁㄴasd123", "AlphaNumericSpecialCharOnly"),
                Arguments.of("password", "123123123", "PasswordComplexity"),


                Arguments.of("confirmPassword", null, "NotBlank"),
                Arguments.of("confirmPassword", "", "NotBlank"),
                Arguments.of("confirmPassword", " ", "NotBlank"),
                Arguments.of("confirmPassword", "          ", "NotBlank"),
                Arguments.of("confirmPassword", "\n", "NotBlank"),
                Arguments.of("confirmPassword", "\t", "NotBlank"),

                Arguments.of("passwordConfirmed", "password", "AssertTrue")
        );
    }

    @BeforeEach
    void setUp() {
        registerDTO = new RegisterDTO();
        registerDTO.setUsername("testUsername");
        registerDTO.setLoginId("testLoginId");
        registerDTO.setEmail("testEmail@email.com");
        registerDTO.setPassword("testPassword123");
        registerDTO.setConfirmPassword("testPassword123");
    }

    @Test
    @DisplayName("GET 회원가입 폼")
    void registerForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register-form"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @DisplayName("POST 회원가입 정상")
    void registerSubmit_normal() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .flashAttr("user", registerDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/register/success/*"))
                .andExpect(request().sessionAttribute(SessionConst.REGISTER_RESPONSE_DTO,
                        Matchers.hasProperty("username", Matchers.is(registerDTO.getUsername()))));
    }

    @Test
    @DisplayName("POST 회원가입 중복 아이디 예외 체크")
    void registerSubmit_Exception() throws Exception {

        registerService.addUser(registerDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .flashAttr("user", registerDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("register-form"))
                .andExpect(model().attributeHasFieldErrorCode("user", "loginId", "duplicateLoginId"));

    }

    @ParameterizedTest
    @MethodSource("provideErrorInputs")
    @DisplayName("POST 회원가입 오류 검증")
    void registerSubmit_Error(String field, String value, String errorCode) throws Exception {

        switch (field) {
            case "username" -> registerDTO.setUsername(value);
            case "loginId" -> registerDTO.setLoginId(value);
            case "email" -> registerDTO.setEmail(value);
            case "password" -> registerDTO.setPassword(value);
            case "confirmPassword" -> registerDTO.setConfirmPassword(value);
            case "passwordConfirmed" -> registerDTO.setConfirmPassword(value);
        }

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .flashAttr("user", registerDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("register-form"))
                .andExpect(model().attributeHasFieldErrorCode("user", field, errorCode));
    }

    @Test
    @DisplayName("GET 성공페이지 정상 작동")
    void registerSuccess_normal() throws Exception {
        RegisterResponseDTO registerResponseDTO = registerService.addUser(registerDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/register/success/{userId}", registerResponseDTO.getId())
                        .sessionAttr(SessionConst.REGISTER_RESPONSE_DTO, registerResponseDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("register-success"))
                .andExpect(model().attribute("username", registerDTO.getUsername()));
    }

    @Test
    @DisplayName("GET 성공페이지 세션 없음 (비정상)")
    void registerSuccess_no_session() throws Exception {
        RegisterResponseDTO registerResponseDTO = registerService.addUser(registerDTO);
        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setId(123L);
        sessionUserDTO.setLoginId("testLoginId");
        sessionUserDTO.setUsername("testUsername");

        mockMvc.perform(MockMvcRequestBuilders.get("/register/success/{userId}", registerResponseDTO.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(MockMvcRequestBuilders.get("/register/success/{userId}", registerResponseDTO.getId())
                        .sessionAttr(SessionConst.SESSION_USER_DTO, sessionUserDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("GET 성공페이지 세션 DTO와 id 다름 (비정상)")
    void registerSuccess_not_match_userId() throws Exception {
        RegisterResponseDTO registerResponseDTO = registerService.addUser(registerDTO);
        registerResponseDTO.setId(registerResponseDTO.getId() + 1L);

        mockMvc.perform(MockMvcRequestBuilders.get("/register/success/{userId}", registerResponseDTO.getId() - 1L)
                        .sessionAttr(SessionConst.REGISTER_RESPONSE_DTO, registerResponseDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

}
