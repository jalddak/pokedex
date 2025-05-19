package pokemon.pokedex.admin.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import pokemon.pokedex._global.LogMessage;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.domain.Role;
import pokemon.pokedex.user.dto.SessionUserDTO;

@Slf4j
@Component
public class NormalUserOnlyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        SessionUserDTO sessionUserDTO = (SessionUserDTO) request.getAttribute(SessionConst.SESSION_USER_DTO);
        if (sessionUserDTO.getRole() == Role.ADMIN) {
            response.sendRedirect(request.getContextPath() + "/admin");
            log.debug(LogMessage.ADMIN_REQUEST_LOG + ", Redirecting to /admin", request.getRequestURI(), sessionUserDTO.getLoginId());
            return false;
        }

        log.debug(LogMessage.NORMAL_REQUEST_LOG, request.getRequestURI(), sessionUserDTO.getLoginId());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
