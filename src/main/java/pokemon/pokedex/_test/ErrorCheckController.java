package pokemon.pokedex._test;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

/**
 * 에러 페이지 확인용. 확인하고 나면 @Controller 주석처리
 */

@Slf4j
@Controller
@Profile({"dev", "memory"})
public class ErrorCheckController {

    @GetMapping("/error-400")
    public String error400(HttpServletResponse response) throws IOException {
        log.debug("error-400");
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        return "error/4xx";
    }

    @GetMapping("/error-404")
    public String error404(HttpServletResponse response) throws IOException {
        log.debug("error-404");
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return "error/4xx";
    }

    @GetMapping("/error-500")
    public String error500(HttpServletResponse response) throws IOException {
        log.debug("error-500");
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return "error/4xx";
    }

    @GetMapping("/error-502")
    public String error502(HttpServletResponse response) throws IOException {
        log.debug("error-502");
        response.sendError(HttpServletResponse.SC_BAD_GATEWAY);
        return "error/4xx";
    }

}
