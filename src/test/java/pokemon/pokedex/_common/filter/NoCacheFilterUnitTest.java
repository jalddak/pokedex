package pokemon.pokedex._common.filter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pokemon.pokedex.user.controller.LoginController;
import pokemon.pokedex.user.service.LoginService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({LoginController.class})
class NoCacheFilterUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoginService loginService;

    @Test
    public void noCacheFilter() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
                .andExpect(MockMvcResultMatchers.header().string("Pragma", "no-cache"));
    }

}