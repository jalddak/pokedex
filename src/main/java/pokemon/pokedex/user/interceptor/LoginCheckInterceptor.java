package pokemon.pokedex.user.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.dto.CheckedUserDTO;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(SessionConst.CHECKED_USER_DTO) == null) {
            log.debug("게스트(비로그인) 접속: {}", request.getRequestURI());
            response.sendRedirect(request.getContextPath() + "/login?redirectURI=" + request.getRequestURI());
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        HttpSession session = request.getSession(false);
        CheckedUserDTO checkedUserDTO = (CheckedUserDTO) session.getAttribute(SessionConst.CHECKED_USER_DTO);
        modelAndView.addObject("user", checkedUserDTO);
    }
}
