package com.slack.slack.domain.user;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.slack.slack.error.exception.*;

import com.slack.slack.mail.MailService;
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
     * 회원의 이메일을 받아 회원가입을 위한 이메일을 발송합니다.
     * 이때, 회원가입 용도의 5분 짜리 토큰을 생성하여 함께 발송합니다.
     *
     * @ param String email 유저의 이메일을 받습니다.
     * @ exception UserNotFoundException : 사용자가 검색되지 않을 경우 반환합니다.
     * @ exception InvalidInputException : 이메일의 형식이 잘못되었을 경우 반환합니다.
     */
    @GetMapping("/join/{email}")
    public ResponseEntity join_get(@PathVariable String email, Model model, Locale locale) throws UserNotFoundException, InvalidInputException {

        mailService.sendWelcomeMail(email, locale);

        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("{email}")
                        .buildAndExpand(email)
                        .toUri()
        );

        return new ResponseEntity(null, header, HttpStatus.ACCEPTED);
    }

    /**
     * 토큰이 유효할 경우, 유효성 검사를 진행 한 후, 회원가입을 승인합니다.
     *
     * @ param String email 유저의 이메일을 받습니다.
     * @ exception InvalidInputException : 이메일의 형식이 잘못되었을 경우 반환합니다.
     * @ exception ResourceConflict : 이메일이 이미 존재하는 경우 반환 합니다.
     * */
    @PostMapping("")
    public ResponseEntity join_post(
            @Valid @RequestBody UserDTO userDTO
            , Model model
            , Locale locale
            , @RequestHeader(value = "X-AUTH-TOKEN") String token
    ) throws InvalidInputException, ResourceConflict {

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

        return new ResponseEntity(mapping, header, HttpStatus.ACCEPTED);
    }

    /**
     * 로그인 성공시, 로그인 이메일, 권한 정보를 토큰에 담아 발급
     *
     * @ param String email 유저의 이메일을 받습니다.
     * @ exception InvalidInputException : 비밀번호가 잘못되었을 경우 반환합니다.
     * @ exception UserNotFoundException : 가입된 사용자를 찾지 못한 경우 반환합니다.
     * */
    @PostMapping("/login")
    public String join_post(@Valid @RequestBody LoginUserDTO userDTO, Model model , Locale locale)
            throws UserNotFoundException, InvalidInputException {

        return userService.login(userDTO);
    }




}
