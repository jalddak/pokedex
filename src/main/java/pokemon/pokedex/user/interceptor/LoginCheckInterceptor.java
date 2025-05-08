package pokemon.pokedex.user.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import pokemon.pokedex._global.session.SessionConst;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.service.UserService;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    private final UserService userService;

    public LoginCheckInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();

        if (request.getAttribute(SessionConst.SESSION_USER_DTO) == null) {
            log.debug("비로그인 사용자 접속, 로그인 페이지로 안내: {}", requestURI);
            response.sendRedirect(request.getContextPath() + "/login?redirectURI=" + requestURI);
            return false;
        }

        SessionUserDTO sessionUserDTO = (SessionUserDTO) request.getAttribute(SessionConst.SESSION_USER_DTO);
        SessionUserDTO realUserDTO = userService.getRealUserDTO(sessionUserDTO);

        try {
            HttpSession session = request.getSession(false);

            if (realUserDTO == null) {
                log.debug("삭제된 유저 접속, 로그인 페이지로 안내: {}", requestURI);
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/login?redirectURI=" + requestURI);
                return false;
            }

            session.setAttribute(SessionConst.SESSION_USER_DTO, realUserDTO);
            request.setAttribute(SessionConst.SESSION_USER_DTO, realUserDTO);

        } catch (Exception e) {
            log.warn("LoginCheckInterceptor 진행 중 세션 무효화 발생, 로그인 페이지로 이동", e);
            request.removeAttribute(SessionConst.SESSION_USER_DTO);
            response.sendRedirect(request.getContextPath() + "/login?redirectURI=" + requestURI);
            return false;
        }
        return true;
    }
}
