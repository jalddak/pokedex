package pokemon.pokedex._common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import pokemon.pokedex._global.SessionConst;

@Slf4j
@Controller
public class HomeController {

    @GetMapping
    public String home(HttpServletRequest request) {
        log.debug("HomeController: home");

        if (request.getAttribute(SessionConst.SESSION_USER_DTO) == null) {
            return "home";
        }

        return "login-home";
    }
}
