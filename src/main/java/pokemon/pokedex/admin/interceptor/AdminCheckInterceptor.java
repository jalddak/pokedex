package pokemon.pokedex.admin.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.domain.Role;
import pokemon.pokedex.user.dto.CheckedUserDTO;

@Slf4j
public class AdminCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        CheckedUserDTO checkedUserDTO = (CheckedUserDTO) session.getAttribute(SessionConst.CHECKED_USER_DTO);

        if (checkedUserDTO.getRole() != Role.ADMIN) {
            log.debug("관리자 페이지에 일반 유저 접근: {}", request.getRequestURI());
            response.sendRedirect(request.getContextPath() + "/admin/alert");
            return false;
        }
        return true;
    }
}
