package pokemon.pokedex.user.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import pokemon.pokedex._global.LogMessage;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginCheckInterceptor implements HandlerInterceptor {

    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();

        if (request.getAttribute(SessionConst.SESSION_USER_DTO) == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirectURI=" + requestURI);
            log.debug(LogMessage.GUEST_REQUEST_LOG + ", Redirecting to /login", requestURI, request.getRemoteAddr());
            return false;
        }

        SessionUserDTO sessionUserDTO = (SessionUserDTO) request.getAttribute(SessionConst.SESSION_USER_DTO);
        SessionUserDTO realUserDTO = userService.getRealUserDTO(sessionUserDTO);

        HttpSession session = request.getSession(false);
        try {

            if (realUserDTO == null) {
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/login?redirectURI=" + requestURI);
                log.debug("User not in DB or deleted, loginId: {}, Redirecting to /login", sessionUserDTO.getId());
                return false;
            }

            session.setAttribute(SessionConst.SESSION_USER_DTO, realUserDTO);
            request.setAttribute(SessionConst.SESSION_USER_DTO, realUserDTO);

        } catch (Exception e) {
            request.removeAttribute(SessionConst.SESSION_USER_DTO);
            response.sendRedirect(request.getContextPath() + "/login?redirectURI=" + requestURI);
            log.warn(LogMessage.SESSION_EXCEPTION_LOG + ", Redirecting to /login", e);
            return false;
        }

        log.debug(LogMessage.LOGGED_IN_USER_REQUEST_LOG, requestURI, realUserDTO.getLoginId());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
