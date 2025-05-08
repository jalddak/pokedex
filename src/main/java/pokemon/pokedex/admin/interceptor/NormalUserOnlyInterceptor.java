package pokemon.pokedex.admin.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import pokemon.pokedex._global.session.SessionConst;
import pokemon.pokedex.user.domain.Role;
import pokemon.pokedex.user.dto.SessionUserDTO;

@Slf4j
public class NormalUserOnlyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        SessionUserDTO sessionUserDTO = (SessionUserDTO) request.getAttribute(SessionConst.SESSION_USER_DTO);
        if (sessionUserDTO.getRole() != Role.ADMIN) return true;

        log.debug("관리자가 관리자 경고 페이지에 접근: {}", sessionUserDTO.getLoginId());
        response.sendRedirect(request.getContextPath() + "/admin");
        return false;
    }
}
