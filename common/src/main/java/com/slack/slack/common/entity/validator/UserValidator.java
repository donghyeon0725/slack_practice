package com.slack.slack.common.entity.validator;

import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.code.Key;
import com.slack.slack.common.code.RegularExpression;
import com.slack.slack.common.dto.user.UserCommand;
import com.slack.slack.common.exception.InvalidInputException;
import com.slack.slack.common.exception.ResourceConflict;
import com.slack.slack.common.exception.UnauthorizedException;
import com.slack.slack.common.repository.TeamMemberRepository;
import com.slack.slack.common.repository.UserRepository;
import com.slack.slack.common.util.TokenManager;
import org.springframework.stereotype.Component;

@Component
public class UserValidator extends PermissionValidator {
    private final UserRepository userRepository;

    private final TokenManager tokenManager;

    public UserValidator(TeamMemberRepository teamMemberRepository, UserRepository userRepository, TokenManager tokenManager) {
        super(teamMemberRepository);
        this.userRepository = userRepository;
        this.tokenManager = tokenManager;
    }

    public void checkAlreadyJoined(String email) {
        // 이메일이 중복되지 않았는지
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new ResourceConflict(ErrorCode.EMAIL_DUPLICATION);
        });
    }

    public void checkTokenIsValid(UserCommand userCommand, String token) {
        // 토큰이 유효한지
        if (!tokenManager.isValidateToken(token, Key.JOIN_KEY))
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        // 비밀번호가 유효한지
        if (!RegularExpression.PW_ALPHA_NUM_SPE.isValidate(userCommand.getPassword()))
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        String tokenEmail = tokenManager.get(token, Key.JOIN_KEY).get(0);

        // 토큰을 발급 받은 자가 다른 사람인지
        if (!userCommand.getEmail().equals(tokenEmail))
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

    }
}
