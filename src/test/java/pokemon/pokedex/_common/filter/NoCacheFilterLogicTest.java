package pokemon.pokedex._common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class NoCacheFilterLogicTest {
    private NoCacheFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new NoCacheFilter();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
    }

    @Test
    void doFilter() throws IOException, ServletException {
        // when
        filter.doFilter(request, response, filterChain);

        // then
        verify(response).setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        verify(response).setHeader("Pragma", "no-cache");
        verify(response).setDateHeader(eq("Expires"), eq(0L));
        verify(filterChain).doFilter(request, response);
    }
}
