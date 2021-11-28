package com.slack.slack.domain.service.impl;

import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.dto.user.LoginUserDTO;
import com.slack.slack.common.dto.user.UserDTO;
import com.slack.slack.common.entity.User;
import com.slack.slack.common.entity.UserRole;
import com.slack.slack.common.util.JwtTokenProvider;
import com.slack.slack.common.util.TokenManager;
import com.slack.slack.common.entity.Role;
import com.slack.slack.common.repository.RoleRepository;
import com.slack.slack.common.entity.BaseCreateEntity;
import com.slack.slack.common.repository.UserRepository;
import com.slack.slack.domain.service.UserService;
import com.slack.slack.error.exception.*;
import com.slack.slack.common.code.Key;
import com.slack.slack.common.code.RegularExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    /* token 관리자 */
    private final TokenManager tokenManager;

    /* 비밀번호 encoder */
    private final PasswordEncoder passwordEncoder;

    /* 토큰 생성을 위한 모듈 */
    private final JwtTokenProvider jwtTokenProvider;

    private final RoleRepository roleRepository;


    /**
     * 토큰이 유효할 경우, 유효성 검사를 진행 한 후, 회원가입을 승인합니다.
     *
     * @ param String email 유저의 이메일을 받습니다.
     * @ exception InvalidInputException : 이메일의 형식이 잘못되었을 경우 반환합니다.
     * @ exception ResourceConflict : 이메일이 이미 존재하는 경우 반환 합니다.
     * */
    @Transactional
    public User save(String token, UserDTO userDTO) throws InvalidInputException, ResourceConflict, UnauthorizedException {

        // 토큰이 유효한지
        tokenManager.checkValidation(token, Key.JOIN_KEY);

        // 비밀번호가 유효한지
        RegularExpression.getChecker().check(RegularExpression.pw_alpha_num_spe, userDTO.getPassword());

        // 이메일이 중복되지 않았는지
        userRepository.findByEmail(userDTO.getEmail()).ifPresent(user -> {
            throw new ResourceConflict(ErrorCode.EMAIL_DUPLICATION);
        });

        // 토큰을 발급 받은 자가 다른 사람인지
        userDTO.checkDifferentEmail(tokenManager.get(token, Key.JOIN_KEY).get(0));

        Role role = roleRepository.findByRoleName(com.slack.slack.common.code.Role.ROLE_USER.getRole());

        User user = userRepository.save(
                User.builder()
                    .email(userDTO.getEmail())
                    .password(passwordEncoder.encode(userDTO.getPassword()))
                    .name(userDTO.getName())
                    .date(new Date())
                    .baseCreateEntity(BaseCreateEntity.now(userDTO.getEmail()))
                    .build()
        );

        user.getUserRoles().add(
                UserRole.builder().role(role).user(user).build()
        );

        return user;
    }

    /**
     * 로그인 성공시, 로그인 이메일, 권한 정보를 토큰에 담아 발급
     *
     * @ param String email 유저의 이메일을 받습니다.
     * @ exception InvalidInputException : 비밀번호가 잘못되었을 경우 반환합니다.
     * @ exception UserNotFoundException : 가입된 사용자를 찾지 못한 경우 반환합니다.
     * */
    @Transactional
    public String login(LoginUserDTO userDTO) throws UserNotFoundException, InvalidInputException {
        User member = userRepository.findByEmail(userDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.INVALID_INPUT_VALUE));

        member.matchePassword(userDTO.getPassword(), passwordEncoder);


        return jwtTokenProvider.createToken(member.getEmail(),
                member
                        .getUserRoles()
                        .stream()
                        .map(s -> s.getRole().getRoleName())
                        .map(SimpleGrantedAuthority::new).collect(Collectors.toList())
        );
    }

    /**
     * 유저 리스트 반환
     *
     * @ param String token 토큰을 받습니다.
     * @ param UserDTO userDTO 검색할 이메일을 받습니다.
     * @ exception UserNotFoundException : 가입된 사용자를 찾지 못한 경우 반환합니다.
     * */
    @Override
    @Transactional
    public List<User> retrieveUserList(String email) {
        return userRepository.findTop5ByEmailContaining(email)
                .orElse(new ArrayList<>());
    }

}
