package pokemon.pokedex._common;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pokemon.pokedex._global.SessionConst;
import pokemon.pokedex.admin.AdminConfig;
import pokemon.pokedex.admin.AdminController;
import pokemon.pokedex.admin.service.AdminService;
import pokemon.pokedex.user.domain.AdminRequestStatus;
import pokemon.pokedex.user.dto.CheckedUserDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = AdminController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AdminConfig.class))
class CommonControllerAdviceUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @Test
    void illegalArgumentException() throws Exception {
        CheckedUserDTO checkedUserDTO = new CheckedUserDTO();

        doThrow(new IllegalArgumentException())
                .when(adminService)
                .changeAdminRequestStatus(any(CheckedUserDTO.class), any(AdminRequestStatus.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/alert")
                        .sessionAttr(SessionConst.CHECKED_USER_DTO, checkedUserDTO))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("error/4xx"));

        ArgumentCaptor<CheckedUserDTO> captor = ArgumentCaptor.forClass(CheckedUserDTO.class);
        verify(adminService).changeAdminRequestStatus(captor.capture(), any(AdminRequestStatus.class));

        CheckedUserDTO usedCheckedUserDTO = captor.getValue();
        assertThat(usedCheckedUserDTO).isEqualTo(checkedUserDTO);

    }

}