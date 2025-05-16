package pokemon.pokedex.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pokemon.pokedex._global.LogMessage;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.service.UserService;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping
    public String home() {
        return "admin/home";
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
        log.debug("Logout successful, Redirecting to /admin");
        return "redirect:/admin";
    }

    @GetMapping("/alert")
    public String alert() {
        return "admin/alert";
    }

    @PostMapping("/alert")
    public String requestAdminRole(HttpServletRequest request) {
        SessionUserDTO sessionUserDTO = (SessionUserDTO) request.getAttribute(SessionConst.SESSION_USER_DTO);

        log.debug("Request admin role, userId: {}, loginId: {}", sessionUserDTO.getId(), sessionUserDTO.getLoginId());
        userService.requestAdminRole(sessionUserDTO.getId());

        return "redirect:/admin/alert";
    }
}
