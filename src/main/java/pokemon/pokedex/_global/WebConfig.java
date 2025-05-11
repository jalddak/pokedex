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

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final NoCacheFilter noCacheFilter;
    private final LoginUserInjectInterceptor loginUserInjectInterceptor;
    private final LoginCheckInterceptor loginCheckInterceptor;
    private final AdminCheckInterceptor adminCheckInterceptor;
    private final NormalUserOnlyInterceptor normalUserOnlyInterceptor;
    private final GuestOnlyInterceptor guestOnlyInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(loginUserInjectInterceptor)
                .order(0)
                .addPathPatterns("/**")
                .excludePathPatterns("/assets/**", "/login", "/register/**", "/logout", "/admin/logout");

        registry.addInterceptor(loginCheckInterceptor)
                .order(1)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/logout");

        registry.addInterceptor(adminCheckInterceptor)
                .order(2)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/logout", "/admin/alert");

        registry.addInterceptor(normalUserOnlyInterceptor)
                .order(3)
                .addPathPatterns("/admin/alert");

        registry.addInterceptor(guestOnlyInterceptor)
                .addPathPatterns("/login", "/register/**");

    }

    @Bean
    public FilterRegistrationBean noCacheFilterBean() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(noCacheFilter);
        filterRegistrationBean.addUrlPatterns("/login", "/register", "/admin/*");

        return filterRegistrationBean;
    }
}
