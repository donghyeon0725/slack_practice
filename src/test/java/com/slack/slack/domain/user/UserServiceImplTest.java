package com.slack.slack.domain.user;

import com.slack.slack.appConfig.security.JwtTokenProvider;
import com.slack.slack.appConfig.security.TokenManager;
import com.slack.slack.appConfig.security.TokenProvider;
import com.slack.slack.appConfig.security.UserDetailServiceImpl;
import com.slack.slack.appConfig.security.domain.entity.Role;
import com.slack.slack.appConfig.security.domain.repository.RoleRepository;
import com.slack.slack.error.exception.InvalidInputException;
import com.slack.slack.error.exception.ResourceConflict;
import com.slack.slack.error.exception.UnauthorizedException;
import com.slack.slack.error.exception.UserNotFoundException;
import com.slack.slack.mail.MailManager;
import com.slack.slack.mail.MailServiceImpl;
import com.slack.slack.system.Key;
import com.slack.slack.system.Time;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 통합테스트 (시스템 인증)
 *
 * 회원가입메일(토큰 발급) - 회원가입 - 로그인
 *
 * @author 김동현
 */
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceImplTest {

    private String joinToken;
    private String email = "test1234@test.com";
    private String password = "@qwed2009@";

    private TokenManager tokenManager;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(null);

    {
        try {
            // Java Reflection 을 이용해서 Field 주입 (@Value 어노테이션으로 주입한 필드라 이 작업이 필요 합니다.)
            Class<?> clazz = Class.forName("com.slack.slack.appConfig.security.TokenProvider");
            Field secretKey = clazz.getDeclaredField("secretKey");
            secretKey.setAccessible(true);

            Constructor<?> declaredConstructor = clazz.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            Object instance = declaredConstructor.newInstance();

            secretKey.set(instance, "webfirewood");
            TokenProvider tokenProvider = (TokenProvider)instance;

            tokenManager = new TokenManager(tokenProvider);

            Class<?> provider = Class.forName("com.slack.slack.appConfig.security.JwtTokenProvider");
            Field providerSecretKey = provider.getDeclaredField("secretKey");
            providerSecretKey.setAccessible(true);

            Constructor<?> providerConstructor = provider.getDeclaredConstructor(UserDetailServiceImpl.class);
            providerConstructor.setAccessible(true);
            Object providerInstance = providerConstructor.newInstance(new UserDetailServiceImpl(null));

            providerSecretKey.set(providerInstance, "webfirewood");

            jwtTokenProvider = (JwtTokenProvider)providerInstance;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(1)
    @DisplayName("회원 가입 용도 토큰 생성")
    void create_join_token() {
        // TODO 회원 가입 토큰이 정상 생성 되는지 확인
        joinToken = "bearer " + tokenManager.createToken(Key.JOIN_KEY, Time.FIVE_MINUTE, Arrays.asList(email));

        assertNotEquals("", joinToken);
        assertNotNull(joinToken);
    }

    @Order(2)
    @Test
    @DisplayName("회원 가입 용도 메일 전송")
    void join_mail(
            @Mock MessageSource messageSource,
            @Mock TokenManager tokenManager,
            @Mock MailManager mailManager
    ) {
        // TODO 회원 가입 메일을 보낼 때, Key.JOIN_KEY 와 Time.FIVE_MINUTE 토큰으로 메일을 보내는지 검증

        // given
        MailServiceImpl service = new MailServiceImpl(messageSource, tokenManager, mailManager);
        Locale locale = Locale.getDefault();

        // when
        service.sendWelcomeMail(email, locale);

        // then
        verify(tokenManager, times(1)).createToken(Key.JOIN_KEY, Time.FIVE_MINUTE, Arrays.asList(email));
    }


    @Order(3)
    @Test
    @DisplayName("회원 가입 case 1")
    void join_case_1(@Mock RoleRepository roleRepository, @Mock UserRepository userRepository) {
        // TODO 토큰 유효성 검사에서 에러 없이, 통과하는지 검사
        // TODO 토큰을 아무거나 집어넣었을 때 Exception 을 띄우는지 검사


        // given
        UserServiceImpl userService = new UserServiceImpl(userRepository, tokenManager, passwordEncoder, jwtTokenProvider, roleRepository);

        String invalidToken = "testToken";

        UserDTO validUserDto = UserDTO.builder().email(this.email).password(this.password).date(new Date()).name("유저").build();

        // stubbing
        when(roleRepository.findByRoleName(com.slack.slack.system.Role.ROLE_USER.getRole())).thenReturn(Role.builder().roleName("ROLE_NAME").build());
        // 받은 거 그대로 리턴
        when(userRepository.save(any())).thenAnswer((Answer) invocation -> invocation.getArguments()[0]);
        when(userRepository.findByEmail(this.email)).thenReturn(Optional.empty());

        // when
        assertAll(
                () -> assertNotNull(userService.save(this.joinToken, validUserDto)),
                () -> assertThrows(InvalidInputException.class, () -> userService.save(invalidToken, validUserDto))
        );
    }

    @Order(4)
    @Test
    @DisplayName("회원 가입 case 2")
    void join_case_2(@Mock RoleRepository roleRepository, @Mock UserRepository userRepository) {
        // TODO 비밀번호를 조건을 만족하지 않았을 때 정상 통과하지 않는지


        // given
        UserServiceImpl userService = new UserServiceImpl(userRepository, tokenManager, passwordEncoder, jwtTokenProvider, roleRepository);

        // 특수문자가 없는 경우
        String invalidPassword1 = "qwed2009";
        // 숫자가 없는 경우
        String invalidPassword2 = "@qwedasd@";
        // 길이가 짧은 경우
        String invalidPassword3 = "a1d@";

        UserDTO invalidPwUserDto1 = UserDTO.builder().email(invalidPassword1).password(invalidPassword1).date(new Date()).name("유저").build();
        UserDTO invalidPwUserDto2 = UserDTO.builder().email(invalidPassword2).password(invalidPassword2).date(new Date()).name("유저").build();
        UserDTO invalidPwUserDto3 = UserDTO.builder().email(invalidPassword3).password(invalidPassword3).date(new Date()).name("유저").build();


        assertAll(
                () -> assertThrows(InvalidInputException.class,
                        () -> userService.save(this.joinToken, invalidPwUserDto1)
                ),
                () -> assertThrows(InvalidInputException.class,
                        () -> userService.save(this.joinToken, invalidPwUserDto2)
                ),
                () -> assertThrows(InvalidInputException.class,
                        () -> userService.save(this.joinToken, invalidPwUserDto3)
                )
        );
    }

    @Order(5)
    @Test
    @DisplayName("회원 가입 case 3")
    void join_case_3(@Mock RoleRepository roleRepository, @Mock UserRepository userRepository) {
        // TODO 이메일이 중복되었을 때 문제를 일으키는지 확인

        // given
        UserServiceImpl userService = new UserServiceImpl(userRepository, tokenManager, passwordEncoder, jwtTokenProvider, roleRepository);

        String duplicatedMail = "duplicated@test.com";
        User existUserEntity = User.builder().email(duplicatedMail).build();
        UserDTO invalidUserDto = UserDTO.builder().email(duplicatedMail).password(this.password).date(new Date()).name("유저").build();

        // stubbing
        when(userRepository.findByEmail(duplicatedMail)).thenReturn(Optional.of(existUserEntity));

        assertThrows(ResourceConflict.class, () -> userService.save(this.joinToken, invalidUserDto));
    }

    @Order(6)
    @Test
    @DisplayName("회원 가입 case 4")
    void join_case_4(@Mock RoleRepository roleRepository, @Mock UserRepository userRepository) {
        // TODO 토큰을 발급 받은 사람이 메일을 보낸 자와 동일하지 않을 때 실패하는지

        UserServiceImpl userService = new UserServiceImpl(userRepository, tokenManager, passwordEncoder, jwtTokenProvider, roleRepository);

        String email_other = "newtest1235@test.com";

        UserDTO invalidUserDto = UserDTO.builder().email(email_other).password(this.password).date(new Date()).name("유저").build();

        assertThrows(UnauthorizedException.class, () -> userService.save(this.joinToken, invalidUserDto));
    }

    @Order(7)
    @Test
    @DisplayName("로그인 case 1")
    void login_case_1(@Mock RoleRepository roleRepository, @Mock UserRepository userRepository) {
        // TODO 정상 케이스일 때 토큰을 발급하는지

        // given
        UserServiceImpl userService = new UserServiceImpl(userRepository, tokenManager, passwordEncoder, jwtTokenProvider, roleRepository);

        LoginUserDTO userDTO = LoginUserDTO.builder().email(this.email).password(this.password).build();
        User user = User.builder().email(userDTO.getEmail()).password(passwordEncoder.encode(userDTO.getPassword())).build();

        // when
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));

        assertNotNull(userService.login(userDTO));
    }

    @Order(7)
    @Test
    @DisplayName("로그인 case 2")
    void login_case_2(@Mock RoleRepository roleRepository, @Mock UserRepository userRepository) {
        // TODO 없는 이메일일 때 적절한 에러를 보여주는지

        // given
        UserServiceImpl userService = new UserServiceImpl(userRepository, tokenManager, passwordEncoder, jwtTokenProvider, roleRepository);

        String email_other = "newtest1235@test.com";

        LoginUserDTO userDTO = LoginUserDTO.builder().email(email_other).password(this.password).build();

        // when
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.login(userDTO));
    }

    @Order(8)
    @Test
    @DisplayName("로그인 case 3")
    void login_case_3(@Mock RoleRepository roleRepository, @Mock UserRepository userRepository) {
        // TODO 비밀번호가 틀렸을 때 적절한 에러를 보여주는지

        // given
        UserServiceImpl userService = new UserServiceImpl(userRepository, tokenManager, passwordEncoder, jwtTokenProvider, roleRepository);

        LoginUserDTO userDTO = LoginUserDTO.builder().email(this.email).password(this.password).build();
        User user = User.builder().email(userDTO.getEmail()).password(userDTO.getPassword()).build();

        // when
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));

        assertThrows(InvalidInputException.class, () -> userService.login(userDTO));
    }

}
