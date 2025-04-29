package pokemon.pokedex.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.dto.RegisterDTO;
import pokemon.pokedex.user.dto.RegisterResponseDTO;
import pokemon.pokedex.user.exception.DuplicateLoginIdException;
import pokemon.pokedex.user.service.RegisterService;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegisterController.class)
class RegisterControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterService registerService;

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
                .andExpect(view().name("registerForm"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @DisplayName("POST 회원가입 정상")
    void registerSubmit_normal() throws Exception {

        RegisterResponseDTO registerResponseDTO = new RegisterResponseDTO();
        registerResponseDTO.setUsername(registerDTO.getUsername());
        doReturn(registerResponseDTO).when(registerService).addUser(any(RegisterDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .flashAttr("user", registerDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register/success"))
                .andExpect(request().sessionAttribute(SessionConst.USERNAME, registerDTO.getUsername()));


        ArgumentCaptor<String> captor1 = ArgumentCaptor.forClass(String.class);
        verify(registerService, times(1)).validateDuplicatedLoginId(captor1.capture());
        String usedLoginId = captor1.getValue();
        assertThat(usedLoginId).isEqualTo(registerDTO.getLoginId());

        ArgumentCaptor<RegisterDTO> captor2 = ArgumentCaptor.forClass(RegisterDTO.class);
        verify(registerService, times(1)).addUser(captor2.capture());
        RegisterDTO usedRegisterDTO = captor2.getValue();
        assertThat(usedRegisterDTO).isEqualTo(registerDTO);

    }

    @Test
    @DisplayName("POST 회원가입 중복 아이디 예외 체크")
    void registerSubmit_Exception() throws Exception {
        doThrow(new DuplicateLoginIdException("already exists"))
                .when(registerService).validateDuplicatedLoginId(registerDTO.getLoginId());

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .flashAttr("user", registerDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("registerForm"))
                .andExpect(model().attributeHasFieldErrorCode("user", "loginId", "duplicateLoginId"));

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(registerService, times(1)).validateDuplicatedLoginId(captor.capture());
        String usedLoginId = captor.getValue();
        assertThat(usedLoginId).isEqualTo(registerDTO.getLoginId());

        verify(registerService, never()).addUser(any());
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
                .andExpect(view().name("registerForm"))
                .andExpect(model().attributeHasFieldErrorCode("user", field, errorCode));

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(registerService, times(1)).validateDuplicatedLoginId(captor.capture());
        String usedLoginId = captor.getValue();
        assertThat(usedLoginId).isEqualTo(registerDTO.getLoginId());

        verify(registerService, never()).addUser(any());
    }

    @Test
    @DisplayName("GET 성공페이지 정상 작동")
    void registerSuccess_normal() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/register/success")
                        .sessionAttr(SessionConst.USERNAME, "good"))
                .andExpect(status().isOk())
                .andExpect(view().name("registerSuccess"))
                .andExpect(model().attribute("username", "good"));
    }

    @Test
    @DisplayName("GET 성공페이지 세션 없음")
    void registerSuccess_no_session() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/register/success"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(MockMvcRequestBuilders.get("/register/success")
                        .sessionAttr(SessionConst.LOGIN_RESPONSE_DTO, "something"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}