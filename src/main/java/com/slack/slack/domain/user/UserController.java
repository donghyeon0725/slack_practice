package com.slack.slack.domain.user;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.slack.slack.domain.team.Team;
import com.slack.slack.error.exception.*;

import com.slack.slack.mail.MailService;
import com.slack.slack.requestmanager.ResponseFilterManager;
import com.slack.slack.requestmanager.ResponseHeaderManager;
import io.swagger.annotations.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    // 간단한 필터 생성
    private final SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "email");
    // 위 필터를 우리가 사용 가능한 형태로 변경. UserInfo 을 대상으로 filter를 적용하겠다는 의미
    private final FilterProvider filters = new SimpleFilterProvider().addFilter("User", filter);

    /* 유저 서비스 */
    private UserService userService;

    /* 메일 서비스 */
    private MailService mailService;


    public UserController(UserService userService, MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;
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
            , @ApiParam(value = "지역", required = false) Locale locale
            , @ApiParam(value = "회원 가입용 토큰", required = true) @RequestHeader(value = "X-AUTH-TOKEN") String token
    ) throws InvalidInputException, ResourceConflict, UnauthorizedException {

        User savedUser = userService.save(token, userDTO);

        MappingJacksonValue mapping = new MappingJacksonValue(savedUser);
        mapping.setFilters(filters);

        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(savedUser.getId())
                        .toUri()
        );

        return new ResponseEntity(mapping, header, HttpStatus.CREATED);
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
    public String join_post(
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
            , @ApiParam(value = "토큰", required = true) @RequestHeader(value = "X-AUTH-TOKEN") String token
    ) throws UserNotFoundException, InvalidInputException {

        List<User> user = userService.retrieveUserList(token, email);

        return new ResponseEntity(ResponseFilterManager.setFilters(user, filters)
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.OK);
    }





}
