package pokemon.pokedex.admin.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.domain.Role;
import pokemon.pokedex.user.dto.SessionUserDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminCheckInterceptorLogicTest {

    private AdminCheckInterceptor adminCheckInterceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Object handler;

    @BeforeEach
    void setUp() {
        adminCheckInterceptor = new AdminCheckInterceptor();
    }

    @Test
    @DisplayName("일반 유저 접속")
    void preHandle_normal() throws Exception {
        doReturn("").when(request).getContextPath();

        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setRole(Role.NORMAL);
        doReturn(sessionUserDTO).when(request).getAttribute(SessionConst.SESSION_USER_DTO);

        boolean result = adminCheckInterceptor.preHandle(request, response, handler);

        assertThat(result).isFalse();
        String redirectURI = "/admin/alert";
        verify(response).sendRedirect(redirectURI);

    }

    @Test
    @DisplayName("관리자 접속")
    void preHandle_admin() throws Exception {
        SessionUserDTO sessionUserDTO = new SessionUserDTO();
        sessionUserDTO.setRole(Role.ADMIN);

        doReturn(sessionUserDTO).when(request).getAttribute(SessionConst.SESSION_USER_DTO);

        boolean result = adminCheckInterceptor.preHandle(request, response, handler);

        assertThat(result).isTrue();
    }
}