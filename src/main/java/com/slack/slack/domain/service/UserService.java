package com.slack.slack.domain.service;

import com.slack.slack.common.dto.user.LoginUserDTO;
import com.slack.slack.common.dto.user.UserDTO;
import com.slack.slack.common.entity.User;
import com.slack.slack.error.exception.InvalidInputException;
import com.slack.slack.error.exception.ResourceConflict;
import com.slack.slack.error.exception.UserNotFoundException;

import java.util.List;

public interface UserService {
    /* 토큰 인증 가입 */
    User save(String token, UserDTO userDTO) throws InvalidInputException, ResourceConflict;

    /* 토큰 인증 로그인 */
    String login(LoginUserDTO userDTO) throws UserNotFoundException, InvalidInputException;

    /* 유저 리스트 조회 */
    List<User> retrieveUserList(String email);
}
