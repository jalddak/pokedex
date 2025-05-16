package pokemon.pokedex.user.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import pokemon.pokedex._global.LogMessage;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.dto.SessionUserDTO;

@Slf4j
@Component
public class LoginUserInjectInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SessionConst.SESSION_USER_DTO) != null) {
            try {
                request.setAttribute(SessionConst.SESSION_USER_DTO, session.getAttribute(SessionConst.SESSION_USER_DTO));
                log.debug("Session data injected in request");
            } catch (Exception e) {
                log.warn(LogMessage.SESSION_EXCEPTION_LOG, e);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        SessionUserDTO sessionUserDTO = (SessionUserDTO) request.getAttribute(SessionConst.SESSION_USER_DTO);
        if (sessionUserDTO != null) {
            modelAndView.addObject("user", sessionUserDTO);
            log.debug("Logged-in user, loginId: {}, User data injected in model", sessionUserDTO.getLoginId());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
