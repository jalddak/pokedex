package pokemon.pokedex._common;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.dto.LoginResponseDTO;

@Controller
public class HomeController {

    @GetMapping
    public String home(
            @SessionAttribute(value = SessionConst.LOGIN_RESPONSE_DTO, required = false) LoginResponseDTO loginResponseDTO,
            Model model) {
        if (loginResponseDTO == null) {
            return "home";
        }

        model.addAttribute("user", loginResponseDTO);
        return "loginHome";
    }
}
