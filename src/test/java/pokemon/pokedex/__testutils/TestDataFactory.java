package pokemon.pokedex.__testutils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pokemon.pokedex.user.domain.User;
import pokemon.pokedex.user.dto.LoginDTO;
import pokemon.pokedex.user.dto.RegisterDTO;

import java.util.List;

public class TestDataFactory {

    public static final List<String> defaultInfos = List.of(TestDataConst.DEFAULT_USERNAME,
            TestDataConst.DEFAULT_LOGIN_ID, TestDataConst.DEFAULT_EMAIL, TestDataConst.DEFAULT_PASSWORD);

    public static final List<String> adminInfos = List.of(TestDataConst.ADMIN_USERNAME,
            TestDataConst.ADMIN_LOGIN_ID, TestDataConst.ADMIN_EMAIL, TestDataConst.ADMIN_PASSWORD);

    public static final List<String> adminRequestInfos = List.of(TestDataConst.ADMIN_REQUEST_USERNAME,
            TestDataConst.ADMIN_REQUEST_LOGIN_ID, TestDataConst.ADMIN_REQUEST_EMAIL, TestDataConst.ADMIN_REQUEST_PASSWORD);

    public static final List<String> alreadySessionInfos = List.of(TestDataConst.ALREADY_SESSION_USERNAME,
            TestDataConst.ALREADY_SESSION_LOGIN_ID, TestDataConst.ALREADY_SESSION_EMAIL, TestDataConst.ALREADY_SESSION_PASSWORD);

    public static final List<String> registerInfos = List.of(TestDataConst.REGISTER_USERNAME,
            TestDataConst.REGISTER_LOGIN_ID, TestDataConst.REGISTER_EMAIL, TestDataConst.REGISTER_PASSWORD);

    public static final List<String> deletedInfos = List.of(TestDataConst.DELETED_USERNAME,
            TestDataConst.DELETED_LOGIN_ID, TestDataConst.DELETED_EMAIL, TestDataConst.DELETED_PASSWORD);


    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static RegisterDTO createRegisterDTO(List<String> infos) {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername(infos.get(0));
        registerDTO.setLoginId(infos.get(1));
        registerDTO.setEmail(infos.get(2));
        registerDTO.setPassword(infos.get(3));
        registerDTO.setConfirmPassword(infos.get(3));
        return registerDTO;
    }

    public static User createUser(List<String> infos) {
        RegisterDTO registerDTO = createRegisterDTO(infos);

        User user = User.createByRegisterDto(registerDTO);
        user.setPassword(encoder.encode(user.getPassword()));
        return user;
    }

    public static LoginDTO createLoginDTO(List<String> infos) {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setLoginId(infos.get(1));
        loginDTO.setPassword(infos.get(3));
        return loginDTO;
    }
}
