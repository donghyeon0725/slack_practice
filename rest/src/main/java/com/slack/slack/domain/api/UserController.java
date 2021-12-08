package com.slack.slack.domain.api;

import com.slack.slack.common.dto.user.LoginUserDTO;
import com.slack.slack.common.dto.user.UserDTO;
import com.slack.slack.common.dto.user.UserReturnDTO;
import com.slack.slack.common.entity.User;
import com.slack.slack.domain.service.UserService;
import com.slack.slack.common.error.exception.*;

import com.slack.slack.common.mail.MailService;
import com.slack.slack.common.util.ResponseHeaderManager;
import io.swagger.annotations.*;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private ModelMapper modelMapper;

    /* 유저 서비스 */
    private UserService userService;

    /* 메일 서비스 */
    private MailService mailService;


    public UserController(UserService userService, MailService mailService, ModelMapper modelMapper) {
        this.userService = userService;
        this.mailService = mailService;
        this.modelMapper = modelMapper;
    }

    /**
     * 토큰이 유효할 경우, 유효성 검사를 진행 한 후, 회원가입을 승인합니다.
     *
     * @ param UserDTO userDTO 유저 정보를 받습니다.
     * @ param String token 회원 가입 용도의 토큰을 발급합니다.
     *
     * @ exception InvalidInputException : 이메일의 형식이 잘못되었을 경우 반환합니다.
     * @ exception ResourceConflict : 이메일이 이미 존재하는 경우 반환 합니다.
     * @ exception UnauthorizedException : 토큰에 대한 권한이 없는 이메일인 경우 반환합니다.
     * */
    @PostMapping("")
    @ApiOperation(value = "회원 가입", notes = "이메일 인증을 통해 회원가입을 진행합니다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "회원 가입을 성공 했습니다.")
            ,@ApiResponse(code = 400, message = "토큰 또는 이메일 형식이 잘못 되었거나, 비밀번호 형식이 잘못 되었습니다.") // InvalidInputException
            ,@ApiResponse(code = 401, message = "토큰을 발급 받은 이메일이 아닙니다.") // UnauthorizedException
            ,@ApiResponse(code = 409, message = "이미 회원 가입한 이메일 입니다.") // ResourceConflict
    })
    public ResponseEntity join_post (
            @ApiParam(value = "유저 정보", required = true)  @Valid @RequestBody UserDTO userDTO
            , @ApiParam(value = "회원 가입용 토큰", required = true) @RequestHeader(value = "Authorization") String token
    ) throws InvalidInputException, ResourceConflict, UnauthorizedException {

        User test = userService.save(token, userDTO);

        UserReturnDTO savedUser = modelMapper.map(test, UserReturnDTO.class);

        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(savedUser.getId())
                        .toUri()
        );

        return new ResponseEntity(savedUser, header, HttpStatus.CREATED);
    }

    /**
     * 회원의 이메일을 받아 회원가입을 위한 이메일을 발송합니다.
     * 이때, 회원가입 용도의 5분 짜리 토큰을 생성하여 함께 발송합니다.
     *
     * @ param String email 유저의 이메일을 받습니다.
     * @ exception UserNotFoundException : 사용자가 검색되지 않을 경우 반환합니다.
     * @ exception InvalidInputException : 이메일의 형식이 잘못되었을 경우 반환합니다.
     */
    @ApiOperation(value = "회원 가입 이메일 발송", notes = "회원 가입 용도의 이메일을 발송합니다. 비동기로 메일을 전송하기 때문에, 메일 발송의 성공 여부는 알 수 없습니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "메일을 발송했습니다.")
    })
    @GetMapping("/join/{email}")
    public ResponseEntity join_get(
            @ApiParam(value = "유저 이메일", required = true) @PathVariable String email, Locale locale) throws MailLoadFailException, InvalidInputException {

        mailService.sendWelcomeMail(email, locale);

        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("{email}")
                        .buildAndExpand(email)
                        .toUri()
        );

        return new ResponseEntity(null, header, HttpStatus.OK);
    }

    /**
     * 로그인 성공시, 로그인 이메일, 권한 정보를 토큰에 담아 발급
     *
     * @ param String email 유저의 이메일을 받습니다.
     * @ exception InvalidInputException : 비밀번호가 잘못되었을 경우 반환합니다.
     * @ exception UserNotFoundException : 가입된 사용자를 찾지 못한 경우 반환합니다.
     * */
    @ApiOperation(value = "로그인", notes = "로그인을 진행합니다.")
    @ApiResponses({
            @ApiResponse(code = 202, message = "메일을 발송했습니다.")
            , @ApiResponse(code = 400, message = "이메일 형식이 잘못 되었거나, 비밀번호 형식이 잘못 되었습니다.") // InvalidInputException
            , @ApiResponse(code = 404, message = "없는 사용자에 대한 가입을 진행 했습니다. ") // UserNotFoundException
    })
    @PostMapping("/login")
    public String login_post(
            @ApiParam(value = "로그인 모델", required = true) @Valid @RequestBody LoginUserDTO userDTO)
            throws UserNotFoundException, InvalidInputException {

        return userService.login(userDTO);
    }

    /**
     * 유저의 이메일 5개를 조회합니다.
     *
     * @ param String email 유저의 이메일을 받습니다.
     * */
    @ApiOperation(value = "유저 이메일을 조회", notes = "유저 이메일을 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 조회")
    })
    @GetMapping("/{email}")
    public ResponseEntity join_post(
            @ApiParam(value = "검색할 이메일", required = true) @PathVariable String email
    ) throws UserNotFoundException, InvalidInputException {

        List<User> users = userService.retrieveUserList(email);

        List<UserReturnDTO> userDTOs = users.stream().map(s->modelMapper.map(s, UserReturnDTO.class)).collect(Collectors.toList());


        return new ResponseEntity(userDTOs
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.OK);
    }





}
