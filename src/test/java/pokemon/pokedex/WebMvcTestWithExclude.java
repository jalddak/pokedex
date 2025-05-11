package pokemon.pokedex;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.annotation.AliasFor;
import pokemon.pokedex._global.WebConfig;
import pokemon.pokedex.admin.interceptor.AdminCheckInterceptor;
import pokemon.pokedex.admin.interceptor.NormalUserOnlyInterceptor;
import pokemon.pokedex.user.interceptor.GuestOnlyInterceptor;
import pokemon.pokedex.user.interceptor.LoginCheckInterceptor;
import pokemon.pokedex.user.interceptor.LoginUserInjectInterceptor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WebMvcTest(excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {
                WebConfig.class,
                AdminCheckInterceptor.class,
                NormalUserOnlyInterceptor.class,
                GuestOnlyInterceptor.class,
                LoginCheckInterceptor.class,
                LoginUserInjectInterceptor.class
        }))
public @interface WebMvcTestWithExclude {

    @AliasFor(annotation = WebMvcTest.class, attribute = "controllers")
    Class<?>[] value() default {};
}
