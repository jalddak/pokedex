package pokemon.pokedex._common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.dto.SessionUserDTO;

@Slf4j
@Controller
public class HomeController {

    @GetMapping
    public String home(HttpServletRequest request) {
        SessionUserDTO sessionUserDTO = (SessionUserDTO) request.getAttribute(SessionConst.SESSION_USER_DTO);
        if (sessionUserDTO == null) {
            log.debug("Guest user visited home, ipAddress: {}", request.getRemoteAddr());
            return "home";
        }
        log.debug("Login user visited home, loginId: {}", sessionUserDTO.getLoginId());
        return "login-home";
    }
}
