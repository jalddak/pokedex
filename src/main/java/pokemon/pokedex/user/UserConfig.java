package pokemon.pokedex.user;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pokemon.pokedex.user.interceptor.GuestOnlyInterceptor;

@Configuration
public class UserConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new GuestOnlyInterceptor())
                .addPathPatterns("/login", "/register");
    }
}
