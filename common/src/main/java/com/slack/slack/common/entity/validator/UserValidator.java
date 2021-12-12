package com.slack.slack.common.entity.validator;

import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.code.Key;
import com.slack.slack.common.code.RegularExpression;
import com.slack.slack.common.dto.user.UserDTO;
import com.slack.slack.common.exception.InvalidInputException;
import com.slack.slack.common.exception.ResourceConflict;
import com.slack.slack.common.exception.UnauthorizedException;
import com.slack.slack.common.repository.UserRepository;
import com.slack.slack.common.util.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    private final TokenManager tokenManager;

    public void validateUserForCreate(String email) {
        // 이메일이 중복되지 않았는지
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new ResourceConflict(ErrorCode.EMAIL_DUPLICATION);
        });
    }

    public void validateUserDTOForCreate(UserDTO userDTO, String token) {
        // 토큰이 유효한지
        if (!tokenManager.isValidateToken(token, Key.JOIN_KEY))
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        // 비밀번호가 유효한지
        if (!RegularExpression.PW_ALPHA_NUM_SPE.isValidate(userDTO.getPassword()))
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        String tokenEmail = tokenManager.get(token, Key.JOIN_KEY).get(0);

        // 토큰을 발급 받은 자가 다른 사람인지
        if (!userDTO.getEmail().equals(tokenEmail))
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

    }
}
