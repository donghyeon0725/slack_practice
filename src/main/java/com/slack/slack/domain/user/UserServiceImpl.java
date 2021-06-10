package com.slack.slack.domain.user;

import com.slack.slack.appConfig.security.JwtTokenProvider;
import com.slack.slack.appConfig.security.TokenManager;
import com.slack.slack.error.exception.*;
import com.slack.slack.system.Key;
import com.slack.slack.system.RegularExpression;
import com.slack.slack.system.Role;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


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


    /**
     * 토큰이 유효할 경우, 유효성 검사를 진행 한 후, 회원가입을 승인합니다.
     *
     * @ param String email 유저의 이메일을 받습니다.
     * @ exception InvalidInputException : 이메일의 형식이 잘못되었을 경우 반환합니다.
     * @ exception ResourceConflict : 이메일이 이미 존재하는 경우 반환 합니다.
     * */
    public User save(String token, UserDTO userDTO) throws InvalidInputException, ResourceConflict, UnauthorizedException {
        boolean isValidToken = tokenManager.isInvalid(token, Key.JOIN_KEY);
        if (!isValidToken) throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        boolean isValidPass = RegularExpression.isValid(RegularExpression.pw_alpha_num_spe, userDTO.getPassword());
        if (!isValidPass) throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        /* 이메일이 이미 존재하는지 */
        boolean isAlreadyEmailExist = userRepository.findByEmail(userDTO.getEmail()).isPresent() ? true : false;
        if (isAlreadyEmailExist)  throw new ResourceConflict(ErrorCode.EMAIL_DUPLICATION);

        /* 이메일이 다르면 */
        boolean isDiffEmail = !userDTO.getEmail().equals(tokenManager.get(token, Key.JOIN_KEY).get(0));
        if (isDiffEmail) throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        return userRepository.save(
                User.builder()
                        .email(userDTO.getEmail())
                        .password(passwordEncoder.encode(userDTO.getPassword()))
                        .name(userDTO.getName())
                        .date(new Date())
                        .roles(Arrays.asList(Role.ROLE_USER.getRole()))
                        .build()
        );
    }

    /**
     * 로그인 성공시, 로그인 이메일, 권한 정보를 토큰에 담아 발급
     *
     * @ param String email 유저의 이메일을 받습니다.
     * @ exception InvalidInputException : 비밀번호가 잘못되었을 경우 반환합니다.
     * @ exception UserNotFoundException : 가입된 사용자를 찾지 못한 경우 반환합니다.
     * */
    public String login(LoginUserDTO userDTO) throws UserNotFoundException, InvalidInputException {
        User member = userRepository.findByEmail(userDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.INVALID_INPUT_VALUE));

        if (!passwordEncoder.matches(userDTO.getPassword(), member.getPassword())) {
            throw new InvalidInputException(ErrorCode.WRONG_PASSWORD);
        }
        return jwtTokenProvider.createToken(member.getEmail(), member.getRoles());
    }

    /**
     * 유저 리스트 반환
     *
     * @ param String token 토큰을 받습니다.
     * @ param UserDTO userDTO 검색할 이메일을 받습니다.
     * @ exception UserNotFoundException : 가입된 사용자를 찾지 못한 경우 반환합니다.
     * */
    @Override
    public List<User> retrieveUserList(String token, String email) {
        userRepository.findByEmail(jwtTokenProvider.getUserPk(token))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        return userRepository.findTop5ByEmailContaining(email)
                .orElse(new ArrayList<>());
    }

}
