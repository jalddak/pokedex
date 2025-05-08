package pokemon.pokedex.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pokemon.pokedex._global.session.SessionConst;
import pokemon.pokedex.user.dto.SessionUserDTO;
import pokemon.pokedex.user.service.UserService;

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
            session.invalidate();
        }
        return "redirect:/admin";
    }

    @GetMapping("/alert")
    public String alert() {
        return "admin/alert";
    }

    @PostMapping("/alert")
    public String requestAdminRole(HttpServletRequest request) {
        SessionUserDTO sessionUserDTO = (SessionUserDTO) request.getAttribute(SessionConst.SESSION_USER_DTO);
        userService.requestAdminRole(sessionUserDTO.getId());

        return "redirect:/admin/alert";
    }
}
