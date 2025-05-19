package pokemon.pokedex.user.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import pokemon.pokedex._global.LogMessage;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.dto.SessionUserDTO;

@Slf4j
@Component
public class GuestOnlyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SessionConst.SESSION_USER_DTO) != null) {
            try {
                SessionUserDTO sessionUserDTO = (SessionUserDTO) session.getAttribute(SessionConst.SESSION_USER_DTO);
                log.debug(LogMessage.LOGGED_IN_USER_REQUEST_LOG + ", Redirecting to /", request.getRequestURI(), sessionUserDTO.getLoginId());
            } catch (Exception e) {
                log.warn(LogMessage.SESSION_EXCEPTION_LOG, e);
            }
            response.sendRedirect(request.getContextPath() + "/");
            return false;
        }
        log.debug(LogMessage.GUEST_REQUEST_LOG, request.getRequestURI(), request.getRemoteAddr());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
