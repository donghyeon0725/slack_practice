package com.slack.slack.domain.user;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.slack.slack.error.exception.*;

import com.slack.slack.mail.MailForm;
import com.slack.slack.mail.MailService;
import com.slack.slack.mail.MailUtil;
import com.slack.slack.system.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.slack.slack.appConfig.security.*;

import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

@RestController
@RequestMapping("/users")
public class UserController {
    // 간단한 필터 생성
    private final SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "email");
    // 위 필터를 우리가 사용 가능한 형태로 변경. UserInfo 을 대상으로 filter를 적용하겠다는 의미
    private final FilterProvider filters = new SimpleFilterProvider().addFilter("User", filter);



    private UserRepository userRepository;
    /**
     * 토큰 생성을 위한 필터
     * */
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;

    /* token 관리자 */
    private TokenManager tokenManager;

    /* 메일 서비스 */
    private MailService mailService;

    /* 다국어 서비스 지원 */
    private MessageSource messageSource;

    public UserController(UserRepository userRepository
            , PasswordEncoder passwordEncoder
            , JwtTokenProvider jwtTokenProvider
            , TokenManager tokenManager
            , MailService mailService
            , MessageSource messageSource) {
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.tokenManager = tokenManager;
        this.mailService = mailService;
        this.messageSource = messageSource;
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
        if (!MailUtil.isValidEmail(email)) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);
        }

        String token = tokenManager.createToken(Key.JOIN_KEY, Time.FIVE_MINUTE, null);
        String subject = messageSource.getMessage("email.welcome", null, locale);

        model.addAttribute("email", email);
        model.addAttribute("token", token);

        MailForm mailForm = MailForm.builder()
                .templatePath("welcome.mustache")
                .htmlText(true)
                .model(model)
                .subject(subject)
                .toAddress(email)
                .build();

        mailService.mailSendWithAsync(mailForm)
                .thenAcceptAsync((s) -> {
                    try {
                        // 성공시 파일 삭제
                        // fileManager.deleteFile(list);
                        System.out.println(s.getDescription());
                    } catch (MailLoadFailException e) {
                        throw new MailLoadFailException(e.getErrorCode());
                    }
                })
                .exceptionally( e -> {
                        System.out.println(e.getMessage());
                        return null;
                    }
                );


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
    ) throws InvalidInputException, ResourceConflict{

        boolean isValidToken = tokenManager.isInvalid(token, Key.JOIN_KEY);
        if (!isValidToken) throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        boolean isValidPass = RegularExpression.isValid(RegularExpression.pw_alpha_num_spe, userDTO.getPassword());
        if (!isValidPass) throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        // 이메일이 이미 존재하는지
        boolean isAlreadyEmailExist = userRepository.findByEmail(userDTO.getEmail()).isPresent() ? true : false;
        if (isAlreadyEmailExist)  throw new ResourceConflict(ErrorCode.EMAIL_DUPLICATION);

        User savedUser = userRepository.save(
                            User.builder()
                                    .email(userDTO.getEmail())
                                    .password(passwordEncoder.encode(userDTO.getPassword()))
                                    .name(userDTO.getName())
                                    .date(new Date())
                                    .roles(Arrays.asList(Role.ROLE_USER.getRole()))
                                    .build()
                        );
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



}
