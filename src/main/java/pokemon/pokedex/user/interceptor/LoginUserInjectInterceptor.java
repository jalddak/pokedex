package pokemon.pokedex.user.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import pokemon.pokedex._global.session.SessionConst;
import pokemon.pokedex.user.dto.SessionUserDTO;

@Slf4j
public class LoginUserInjectInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute(SessionConst.SESSION_USER_DTO) != null) {
                request.setAttribute(SessionConst.SESSION_USER_DTO, session.getAttribute(SessionConst.SESSION_USER_DTO));
            }
        } catch (Exception e) {
            log.warn("LoginUserInjectInterceptor 진행 중 세션 무효화 발생", e);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (request.getAttribute(SessionConst.SESSION_USER_DTO) != null) {
            SessionUserDTO sessionUserDTO = (SessionUserDTO) request.getAttribute(SessionConst.SESSION_USER_DTO);
            log.debug("로그인 된 사용자: {}, model 주입", sessionUserDTO.getLoginId());
            modelAndView.addObject("user", sessionUserDTO);
        }
    }
}
