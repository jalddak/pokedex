package pokemon.pokedex.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.user.dto.RegisterDTO;
import pokemon.pokedex.user.dto.RegisterResponseDTO;
import pokemon.pokedex.user.exception.DuplicateLoginIdException;
import pokemon.pokedex.user.service.RegisterService;

/**
 * register 관련 세션들은 try-catch 해주지 않아도 됨. 독립적임.
 * register url에는 guestOnlyInterceptor 가 적용되어서 SessionUserDTO가 포함되어 삭제 가능성 있는 세션은 통과 불가
 */
@Slf4j
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
            log.debug("LoginId: {} is unique", registerDTO.getLoginId());
        } catch (DuplicateLoginIdException e) {
            bindingResult.rejectValue("loginId", "duplicateLoginId", e.getMessage());
            log.debug("Duplicate loginId: {}: {}", registerDTO.getLoginId(), e.getMessage());
        }

        if (bindingResult.hasErrors()) {
            log.debug("Back to register form: {}", bindingResult);
            return "register-form";
        }

        RegisterResponseDTO registerResponseDTO = registerService.addUser(registerDTO);

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.REGISTER_RESPONSE_DTO, registerResponseDTO);

        log.debug("Register success, userId: {}, Redirecting to /register/success/{}", registerResponseDTO.getId(), registerResponseDTO.getId());
        return "redirect:/register/success/" + registerResponseDTO.getId();
    }

    @GetMapping("/success/{userId}")
    public String registerSuccess(
            @PathVariable Long userId,
            HttpServletRequest request,
            Model model) {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SessionConst.REGISTER_RESPONSE_DTO) == null) {
            log.debug("Session is null or has no register response dto in session.");
            return "redirect:/";
        }

        RegisterResponseDTO registerResponseDTO = (RegisterResponseDTO) session.getAttribute(SessionConst.REGISTER_RESPONSE_DTO);
        if (!registerResponseDTO.getId().equals(userId)) {
            log.debug("PathVariable userId: {} does not match registerResponseDTO's userId: {}", userId, registerResponseDTO.getId());
            return "redirect:/";
        }

        String sessionId = session.getId();
        session.invalidate();
        log.debug("Invalidated session, sessionId: {}", sessionId);
        model.addAttribute("username", registerResponseDTO.getUsername());
        return "register-success";
    }
}
