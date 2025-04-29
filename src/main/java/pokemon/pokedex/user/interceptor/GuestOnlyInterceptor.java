package pokemon.pokedex.user.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;
import pokemon.pokedex._global.SessionConst;

public class GuestOnlyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute(SessionConst.LOGIN_RESPONSE_DTO) != null) {
            response.sendRedirect(request.getContextPath() + "/");
            return false;
        }
        return true;
    }
}
