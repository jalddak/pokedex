package pokemon.pokedex._global;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pokemon.pokedex._common.filter.NoCacheFilter;
import pokemon.pokedex.admin.interceptor.AdminCheckInterceptor;
import pokemon.pokedex.admin.interceptor.NormalUserOnlyInterceptor;
import pokemon.pokedex.user.interceptor.GuestOnlyInterceptor;
import pokemon.pokedex.user.interceptor.LoginCheckInterceptor;
import pokemon.pokedex.user.interceptor.LoginUserInjectInterceptor;
import pokemon.pokedex.user.service.UserService;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final UserService userService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new LoginUserInjectInterceptor())
                .order(0)
                .addPathPatterns("/**")
                .excludePathPatterns("/assets/**", "/login", "/register/**", "/logout", "/admin/logout");

        registry.addInterceptor(new LoginCheckInterceptor(userService))
                .order(1)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/logout");

        registry.addInterceptor(new AdminCheckInterceptor())
                .order(2)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/logout", "/admin/alert");

        registry.addInterceptor(new NormalUserOnlyInterceptor())
                .order(3)
                .addPathPatterns("/admin/alert");

        registry.addInterceptor(new GuestOnlyInterceptor())
                .addPathPatterns("/login", "/register/**");

    }

    @Bean
    public FilterRegistrationBean noCacheFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new NoCacheFilter());
        filterRegistrationBean.addUrlPatterns("/login", "/register", "/admin/*");

        return filterRegistrationBean;
    }
}
