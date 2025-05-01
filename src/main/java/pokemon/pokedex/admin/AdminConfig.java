package pokemon.pokedex.admin;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pokemon.pokedex.admin.interceptor.AdminCheckInterceptor;
import pokemon.pokedex.admin.interceptor.NormalUserOnlyInterceptor;
import pokemon.pokedex.user.interceptor.LoginCheckInterceptor;

@Configuration
public class AdminConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
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
    }
}
