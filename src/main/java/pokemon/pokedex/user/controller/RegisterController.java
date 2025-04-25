package pokemon.pokedex.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pokemon.pokedex.SessionConst;
import pokemon.pokedex.user.dto.RegisterDTO;
import pokemon.pokedex.user.dto.RegisterResponseDTO;
import pokemon.pokedex.user.exception.DuplicateLoginIdException;
import pokemon.pokedex.user.service.RegisterService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/register")
public class RegisterController {

    private final RegisterService registerService;

    @GetMapping()
    public String registerForm(@ModelAttribute("user") RegisterDTO registerDTO) {
        return "registerForm";
    }

    @PostMapping()
    public String registerSubmit(
            @ModelAttribute("user") @Valid RegisterDTO registerDTO,
            BindingResult bindingResult,
            HttpServletRequest request) {

        try {
            registerService.validateDuplicatedLoginId(registerDTO.getLoginId());
        } catch (DuplicateLoginIdException e) {
            bindingResult.rejectValue("loginId", "duplicateLoginId", e.getMessage());
        }

        if (bindingResult.hasErrors()) {
            return "registerForm";
        }

        RegisterResponseDTO registerResponseDTO = registerService.addUser(registerDTO);
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.USERNAME, registerResponseDTO.getUsername());
        session.setMaxInactiveInterval(5);

        return "redirect:/register/success";
    }

    @GetMapping("/success")
    public String registerSuccess(
            HttpServletRequest request,
            Model model) {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SessionConst.USERNAME) == null) {
            return "redirect:/";
        }

        String username = (String) session.getAttribute(SessionConst.USERNAME);
        session.invalidate();
        model.addAttribute("username", username);
        return "registerSuccess";
    }
}
