package com.slack.slack.domain.api;

import com.slack.slack.common.dto.user.LoginUserDTO;
import com.slack.slack.common.dto.user.UserDTO;
import com.slack.slack.common.dto.user.UserReturnDTO;
import com.slack.slack.common.entity.User;
import com.slack.slack.common.repository.UserRepository;
import com.slack.slack.domain.service.UserService;
import com.slack.slack.common.exception.*;

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


    @PostMapping("/users")
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

        Integer savedUserId = userService.save(token, userDTO);

        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(savedUserId)
                        .toUri()
        );

        return new ResponseEntity(savedUserId, header, HttpStatus.CREATED);
    }


    @ApiOperation(value = "회원 가입 이메일 발송", notes = "회원 가입 용도의 이메일을 발송합니다. 비동기로 메일을 전송하기 때문에, 메일 발송의 성공 여부는 알 수 없습니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "메일을 발송했습니다.")
    })
    @GetMapping("/users/{email}/join")
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


    @ApiOperation(value = "로그인", notes = "로그인을 진행합니다.")
    @ApiResponses({
            @ApiResponse(code = 202, message = "메일을 발송했습니다.")
            , @ApiResponse(code = 400, message = "이메일 형식이 잘못 되었거나, 비밀번호 형식이 잘못 되었습니다.") // InvalidInputException
            , @ApiResponse(code = 404, message = "없는 사용자에 대한 가입을 진행 했습니다. ") // UserNotFoundException
    })
    @PostMapping("/users/login")
    public String login_post(
            @ApiParam(value = "로그인 모델", required = true) @Valid @RequestBody LoginUserDTO userDTO)
            throws UserNotFoundException, InvalidInputException {

        return userService.login(userDTO);
    }


    @ApiOperation(value = "유저 이메일을 조회", notes = "유저 이메일을 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 조회")
    })
    @GetMapping("/users/{email}")
    public ResponseEntity join_post(
            @ApiParam(value = "검색할 이메일", required = true) @PathVariable String email
    ) throws UserNotFoundException, InvalidInputException {

        List<User> users = userService.retrieveUserList(email);

        List<UserReturnDTO> userDTOs = users.stream().map(s->modelMapper.map(s, UserReturnDTO.class)).collect(Collectors.toList());


        return new ResponseEntity(userDTOs
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.OK);
    }





}
