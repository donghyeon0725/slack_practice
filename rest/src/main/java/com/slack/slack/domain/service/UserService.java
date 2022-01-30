package com.slack.slack.domain.service;

import com.slack.slack.common.dto.user.LoginUserCommand;
import com.slack.slack.common.dto.user.UserCommand;
import com.slack.slack.common.entity.User;
import com.slack.slack.common.exception.InvalidInputException;
import com.slack.slack.common.exception.ResourceConflict;
import com.slack.slack.common.exception.UserNotFoundException;

import java.util.List;

public interface UserService {
    /* 토큰 인증 가입 */
    Integer save(String token, UserCommand userCommand) throws InvalidInputException, ResourceConflict;

    /* 토큰 인증 로그인 */
    String login(LoginUserCommand userDTO) throws UserNotFoundException, InvalidInputException;

    /* 유저 리스트 조회 */
    List<User> retrieveUserList(String email);
}
