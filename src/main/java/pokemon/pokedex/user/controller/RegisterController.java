package pokemon.pokedex.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pokemon.pokedex._global.session.SessionConst;
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
        return "register-form";
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
            return "register-form";
        }

        RegisterResponseDTO registerResponseDTO = registerService.addUser(registerDTO);
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.REGISTER_RESPONSE_DTO, registerResponseDTO);
        session.setMaxInactiveInterval(5);

        return "redirect:/register/success/" + registerResponseDTO.getId();
    }

    @GetMapping("/success/{userId}")
    public String registerSuccess(
            @PathVariable Long userId,
            HttpServletRequest request,
            Model model) {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SessionConst.REGISTER_RESPONSE_DTO) == null) {
            return "redirect:/";
        }

        RegisterResponseDTO registerResponseDTO = (RegisterResponseDTO) session.getAttribute(SessionConst.REGISTER_RESPONSE_DTO);
        if (registerResponseDTO.getId() == null || !registerResponseDTO.getId().equals(userId)) {
            return "redirect:/";
        }

        session.invalidate();
        model.addAttribute("username", registerResponseDTO.getUsername());
        return "register-success";
    }
}
