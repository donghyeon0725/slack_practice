package com.slack.slack.domain.service.impl;

import com.slack.slack.common.code.Roles;
import com.slack.slack.common.dto.user.LoginUserDTO;
import com.slack.slack.common.dto.user.UserDTO;
import com.slack.slack.common.entity.User;
import com.slack.slack.common.entity.UserRole;
import com.slack.slack.common.entity.validator.UserValidator;
import com.slack.slack.common.mail.MailService;
import com.slack.slack.common.repository.UserRepository;
import com.slack.slack.common.util.JwtTokenProvider;
import com.slack.slack.common.util.TokenManager;
import com.slack.slack.common.entity.Role;
import com.slack.slack.common.repository.RoleRepository;
import com.slack.slack.domain.service.UserService;
import com.slack.slack.common.exception.InvalidInputException;
import com.slack.slack.common.exception.ResourceConflict;
import com.slack.slack.common.exception.UnauthorizedException;
import com.slack.slack.common.exception.UserNotFoundException;
import com.slack.slack.common.mail.MailManager;
import com.slack.slack.common.mail.MailServiceImpl;
import com.slack.slack.common.code.Key;
import com.slack.slack.common.code.Time;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
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
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceImplTest {

    private String joinToken;
    private String email = "test1234@test.com";
    private String password = "@qwed2009@";
    private String wrongPassword = "@roneorneskrn";

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private UserService userService;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setField() {
//        ReflectionTestUtils.setField(tokenManager, "secretKey", "webfirewood");
        ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", "webfirewood");
        ReflectionTestUtils.setField(userService, "passwordEncoder", passwordEncoder);
        joinToken = "bearer " + tokenManager.createToken(Key.JOIN_KEY, Time.FIVE_MINUTE, Arrays.asList(email));
    }

    public User createUser(String email, String name, String password, Roles roles) {
        User user = User.builder().email(email).name(name).password(passwordEncoder.encode(password)).build();
        Role findRole = roleRepository.findByRoleName(roles.getRole());

        if (findRole == null) {

            findRole = Role.builder().roleName(roles.getRole()).build();
            roleRepository.save(findRole);
        }

        UserRole userRole = UserRole.builder().role(findRole).user(user).build();
        user.getUserRoles().add(userRole);

        userRepository.save(user);

        em.flush();
        em.clear();

        return user;
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
    void join_mail(@Mock TokenManager mockTokenManager, @Mock MailManager mockMailManager) {
        // TODO 회원 가입 메일을 보낼 때, Key.JOIN_KEY 와 Time.FIVE_MINUTE 토큰으로 메일을 보내는지 검증

        // given
        Locale locale = Locale.getDefault();
        ReflectionTestUtils.setField(mailService, "tokenManager", mockTokenManager);
        ReflectionTestUtils.setField(mailService, "mailManager", mockMailManager);

        // when
        mailService.sendWelcomeMail(email, locale);

        // then
        verify(mockTokenManager, times(1)).createToken(Key.JOIN_KEY, Time.FIVE_MINUTE, Arrays.asList(email));
    }


    @Order(3)
    @Test
    @DisplayName("회원 가입 case 1")
    void join_case_1() {
        // TODO 토큰 유효성 검사에서 에러 없이, 통과하는지 검사
        // TODO 토큰을 아무거나 집어넣었을 때 Exception 을 띄우는지 검사

        // given
        String invalidToken = "testToken";
        UserDTO validUserDto = UserDTO.builder().email(this.email).password(this.password).date(new Date()).name("유저").build();

        Role role_user = Role.builder().roleName("ROLE_NAME").build();
        roleRepository.save(role_user);

        // then
        Integer savedUserId = userService.save(this.joinToken, validUserDto);
        User findUser = userRepository.findById(savedUserId).orElseThrow(() -> new RuntimeException());


        // when then
        assertAll(
                () -> assertEquals(email, findUser.getEmail(), "저장이 잘 되어야 함"),
                () -> assertThrows(InvalidInputException.class, () -> userService.save(invalidToken, validUserDto))
        );
    }

    @Order(4)
    @Test
    @DisplayName("회원 가입 case 2")
    void join_case_2() {
        // TODO 비밀번호를 조건을 만족하지 않았을 때 정상 통과하지 않는지


        // given
        // 특수문자가 없는 경우
        String invalidPassword1 = "qwed2009";
        // 숫자가 없는 경우
        String invalidPassword2 = "@qwedasd@";
        // 길이가 짧은 경우
        String invalidPassword3 = "a1d@";

        UserDTO invalidPwUserDto1 = UserDTO.builder().email(invalidPassword1).password(invalidPassword1).date(new Date()).name("유저").build();
        UserDTO invalidPwUserDto2 = UserDTO.builder().email(invalidPassword2).password(invalidPassword2).date(new Date()).name("유저").build();
        UserDTO invalidPwUserDto3 = UserDTO.builder().email(invalidPassword3).password(invalidPassword3).date(new Date()).name("유저").build();


        // when then
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
    void join_case_3() {
        // TODO 이메일이 중복되었을 때 문제를 일으키는지 확인

        // given
        User user = createUser(email, "유저", password, Roles.ROLE_USER);
        UserDTO invalidUserDto = UserDTO.builder().email(user.getEmail()).password(this.password).date(new Date()).name("유저").build();

        // when then
        assertThrows(ResourceConflict.class, () -> userService.save(this.joinToken, invalidUserDto));
    }

    @Order(6)
    @Test
    @DisplayName("회원 가입 case 4")
    void join_case_4() {
        // TODO 토큰을 발급 받은 사람이 메일을 보낸 자와 동일하지 않을 때 실패하는지

        String email_other = "newtest1235@test.com";

        UserDTO invalidUserDto = UserDTO.builder().email(email_other).password(password).date(new Date()).name("유저").build();

        assertThrows(UnauthorizedException.class, () -> userService.save(joinToken, invalidUserDto));
    }

    @Order(7)
    @Test
    @DisplayName("로그인 case 1")
    void login_case_1() {
        // TODO 정상 케이스일 때 토큰을 발급하는지

        // given
        LoginUserDTO userDTO = LoginUserDTO.builder().email(this.email).password(this.password).build();
        createUser(email, "이름", password, Roles.ROLE_USER);

        // then
        assertNotNull(userService.login(userDTO));
        assertNotEquals("", userService.login(userDTO));
    }

    @Order(7)
    @Test
    @DisplayName("로그인 case 2")
    void login_case_2() {
        // TODO 없는 이메일일 때 적절한 에러를 보여주는지

        // given
        String email_other = "newtest1235@test.com";

        LoginUserDTO userDTO = LoginUserDTO.builder().email(email_other).password(this.password).build();

        // when then
        assertThrows(UserNotFoundException.class, () -> userService.login(userDTO));
    }

    @Order(8)
    @Test
    @DisplayName("로그인 case 3")
    void login_case_3() {
        // TODO 비밀번호가 틀렸을 때 적절한 에러를 보여주는지

        // given
        createUser(email, "이름", password, Roles.ROLE_USER);
        LoginUserDTO userDTO = LoginUserDTO.builder().email(this.email).password(this.wrongPassword).build();

        // when then
        assertThrows(InvalidInputException.class, () -> userService.login(userDTO));
    }

}
