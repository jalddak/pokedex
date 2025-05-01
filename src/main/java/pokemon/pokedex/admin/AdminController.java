package pokemon.pokedex.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.admin.service.AdminService;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.dto.CheckedUserDTO;

@RequestMapping("/admin")
@Controller
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

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
    public String requestAdminRole(@SessionAttribute(SessionConst.CHECKED_USER_DTO) CheckedUserDTO checkedUserDTO,
                                   HttpSession session) {

        AdminRequestStatus newStatus = AdminRequestStatus.REQUESTED;
        adminService.changeAdminRequestStatus(checkedUserDTO, newStatus);

        checkedUserDTO.setAdminRequestStatus(newStatus);
        session.setAttribute(SessionConst.CHECKED_USER_DTO, checkedUserDTO);

        return "redirect:/admin/alert";
    }
}
