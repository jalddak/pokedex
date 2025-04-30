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
import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.dto.LoginResponseDTO;
import pokemon.pokedex.user.exception.LoginFailedException;
import pokemon.pokedex.user.service.LoginService;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
class LoginControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoginService loginService;

    private LoginDTO loginDTO;

    private static Stream<Arguments> provideFieldErrorInputs() {
        return Stream.of(
                Arguments.of("loginId", null, "NotEmpty"),
                Arguments.of("loginId", "", "NotEmpty"),
                Arguments.of("password", null, "NotEmpty")
        );
    }

    @BeforeEach
    void setUp() {
        loginDTO = new LoginDTO();
        loginDTO.setLoginId("testLoginId");
        loginDTO.setPassword("testPassword");
    }

    @Test
    @DisplayName("GET 로그인 폼")
    void loginForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login-form"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @DisplayName("POST 로그인 성공")
    void loginSuccess() throws Exception {
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setLoginId(loginDTO.getLoginId());

        doReturn(loginResponseDTO).when(loginService).checkLogin(any(LoginDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .flashAttr("user", loginDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(request().sessionAttribute(SessionConst.LOGIN_RESPONSE_DTO, loginResponseDTO));

        ArgumentCaptor<LoginDTO> captor = ArgumentCaptor.forClass(LoginDTO.class);
        verify(loginService).checkLogin(captor.capture());

        LoginDTO usedloginDTO = captor.getValue();
        assertThat(usedloginDTO).isEqualTo(loginDTO);
    }

    @Test
    @DisplayName("POST 로그인 실패 - 예외")
    void loginFailed_exception() throws Exception {
        doThrow(new LoginFailedException("login failed")).when(loginService).checkLogin(any(LoginDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .flashAttr("user", loginDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("login-form"))
                .andExpect(model().attributeHasErrors("user"));
    }


    @ParameterizedTest
    @MethodSource("provideFieldErrorInputs")
    @DisplayName("POST 로그인 실패 - 필드에러")
    void loginFailed_error(String field, String value, String errorCode) throws Exception {

        switch (field) {
            case "loginId" -> loginDTO.setLoginId(value);
            case "password" -> loginDTO.setPassword(value);
        }

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .flashAttr("user", loginDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("login-form"))
                .andExpect(model().attributeHasFieldErrorCode("user", field, errorCode));
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logoutSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(MockMvcRequestBuilders.post("/logout")
                        .sessionAttr(SessionConst.LOGIN_RESPONSE_DTO, "something"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(MockMvcRequestBuilders.post("/logout")
                        .sessionAttr(SessionConst.USERNAME, "something"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

}