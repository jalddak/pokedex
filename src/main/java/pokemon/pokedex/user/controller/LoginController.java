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

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("user") LoginDTO loginDTO) {
        log.debug("LoginController: loginForm");
        return "login-form";
    }

    @PostMapping("/login")
    public String login(
            @ModelAttribute("user") @Valid LoginDTO loginDTO,
            BindingResult bindingResult,
            HttpServletRequest request) {
        log.debug("LoginController: login 시도 {}", loginDTO.getLoginId());

        SessionUserDTO sessionUserDTO = null;
        try {
            sessionUserDTO = loginService.checkLogin(loginDTO);
        } catch (LoginFailedException e) {
            bindingResult.reject("loginFailed", e.getMessage());
        }

        if (bindingResult.hasErrors()) {
            return "login-form";
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.SESSION_USER_DTO, sessionUserDTO);
        session.setMaxInactiveInterval(1800);

        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

}
