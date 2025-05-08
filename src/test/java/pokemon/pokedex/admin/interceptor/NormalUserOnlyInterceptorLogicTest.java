package pokemon.pokedex.admin.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pokemon.pokedex._global.session.SessionConst;
import pokemon.pokedex.user.domain.Role;
import pokemon.pokedex.user.dto.SessionUserDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NormalUserOnlyInterceptorLogicTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Object handler;

    private NormalUserOnlyInterceptor normalUserOnlyInterceptor;

    @BeforeEach
    void setUp() {
        normalUserOnlyInterceptor = new NormalUserOnlyInterceptor();
    }


    @Test
    @DisplayName("일반 유저 접속")
    void preHandle_normal() throws Exception {
        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setRole(Role.NORMAL);
        doReturn(sessionUserDTO).when(request).getAttribute(SessionConst.SESSION_USER_DTO);

        boolean result = normalUserOnlyInterceptor.preHandle(request, response, handler);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("관리자 접속")
    void preHandle_admin() throws Exception {
        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setRole(Role.ADMIN);
        doReturn("").when(request).getContextPath();
        doReturn(sessionUserDTO).when(request).getAttribute(SessionConst.SESSION_USER_DTO);

        boolean result = normalUserOnlyInterceptor.preHandle(request, response, handler);

        assertThat(result).isFalse();
        verify(response).sendRedirect("/admin");

    }
}