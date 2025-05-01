package pokemon.pokedex._common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.dto.CheckedUserDTO;

@Slf4j
@Controller
public class HomeController {

    @GetMapping
    public String home(
            @SessionAttribute(value = SessionConst.CHECKED_USER_DTO, required = false) CheckedUserDTO checkedUserDTO,
            Model model) {
        log.debug("HomeController: home");

        if (checkedUserDTO == null) {
            return "home";
        }

        model.addAttribute("user", checkedUserDTO);
        return "login-home";
    }
}
