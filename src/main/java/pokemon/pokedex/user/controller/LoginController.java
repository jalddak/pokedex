package pokemon.pokedex.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pokemon.pokedex._common.session.registry.SessionRegistry;
import pokemon.pokedex._global.LogMessage;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.exception.LoginFailedException;
import pokemon.pokedex.user.service.LoginService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionRegistry sessionRegistry;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("user") LoginDTO loginDTO) {
        return "login-form";
    }

    /**
     * login url은 GuestOnlyInterceptor에 의해서 로그인 정보를 가지고 있는 세션은 요청실패함.
     * 따라서 세션이 관리자에 의해 무효화 될 수 없음. 따라서 try-catch 하지 않아도 됨.
     */
    @PostMapping("/login")
    public String login(
            @ModelAttribute("user") @Valid LoginDTO loginDTO,
            BindingResult bindingResult,
            @RequestParam(defaultValue = "/") String redirectURI,
            HttpServletRequest request) {
        log.debug("Try login, loginId: {}", loginDTO.getLoginId());

        SessionUserDTO sessionUserDTO = null;
        try {
            sessionUserDTO = loginService.checkLogin(loginDTO);
            log.debug("Checked login success, loginId: {}", sessionUserDTO.getLoginId());
        } catch (LoginFailedException e) {
            bindingResult.reject("loginFailed", e.getMessage());
            log.debug("Login failed: {}", e.getMessage());
        }

        if (bindingResult.hasErrors()) {
            log.debug("Back to login form: {}", bindingResult);
            return "login-form";
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            String sessionId = session.getId();
            session.invalidate();
            log.debug("Invalidated previous session, sessionId: {}", sessionId);
        }

        session = request.getSession();
        session.setAttribute(SessionConst.SESSION_USER_DTO, sessionUserDTO);
        log.debug("Created new session, sessionId: {}, sessionTimeout: {}s", session.getId(), session.getMaxInactiveInterval());
        sessionRegistry.addSession(sessionUserDTO.getId(), session);

        log.debug("Login success, loginId: {}, Redirecting to {}", sessionUserDTO.getLoginId(), redirectURI);
        return "redirect:" + redirectURI;
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            try {
                String sessionId = session.getId();
                session.invalidate();
                log.debug("Invalidated session, sessionId: {}", sessionId);
            } catch (Exception e) {
                log.warn(LogMessage.SESSION_EXCEPTION_LOG, e);
            }
        }
        log.debug("Logout Success. Redirecting to /");
        return "redirect:/";
    }

}
