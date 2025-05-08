package pokemon.pokedex.admin.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import pokemon.pokedex._global.session.SessionConst;
import pokemon.pokedex.user.domain.Role;
import pokemon.pokedex.user.dto.SessionUserDTO;

@Slf4j
public class AdminCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        SessionUserDTO sessionUserDTO = (SessionUserDTO) request.getAttribute(SessionConst.SESSION_USER_DTO);
        if (sessionUserDTO.getRole() != Role.ADMIN) {
            log.debug("일반 유저 접속, 경고 페이지로 이동");
            response.sendRedirect(request.getContextPath() + "/admin/alert");
            return false;
        }

        return true;
    }
}
