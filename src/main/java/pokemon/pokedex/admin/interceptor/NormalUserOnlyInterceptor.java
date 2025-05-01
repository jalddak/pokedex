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
public class NormalUserOnlyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        CheckedUserDTO checkedUserDTO = (CheckedUserDTO) session.getAttribute(SessionConst.CHECKED_USER_DTO);

        if (checkedUserDTO.getRole() == Role.ADMIN) {
            log.debug("경고 페이지에 관리자 접근: {}", checkedUserDTO.getLoginId());
            response.sendRedirect(request.getContextPath() + "/admin");
            return false;
        }
        return true;
    }
}
