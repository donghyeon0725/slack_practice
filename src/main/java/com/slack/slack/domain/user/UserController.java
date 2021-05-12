package com.slack.slack.domain.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.slack.slack.error.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.HeadersBuilder;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // 간단한 필터 생성
    private SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name");
    // 위 필터를 우리가 사용 가능한 형태로 변경. UserInfo 을 대상으로 filter를 적용하겠다는 의미
    private FilterProvider filters = new SimpleFilterProvider().addFilter("User", filter);

    @GetMapping("/{email}")
    public ResponseEntity<User> retrieveUser(@PathVariable String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UserNotFoundException(String.format("ID[%s} not found", email));
        }


        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{email}")
                        .buildAndExpand(email)
                        .toUri()
        );

        return new ResponseEntity(user, header, HttpStatus.ACCEPTED);
    }

    @PostMapping("/users")
    public ResponseEntity<User> retrieveUserPost(@Valid @RequestBody UserDTO userDTO) {
        User user = userRepository.findByEmail(userDTO.getEmail());

        if (user == null) {
            throw new UserNotFoundException(String.format("ID[%s} not found", userDTO.getEmail()));
        }

        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{email}")
                        .buildAndExpand(user.getEmail())
                        .toUri()
        );

        return new ResponseEntity(user, header, HttpStatus.ACCEPTED);
    }


    @GetMapping("")
    public ResponseEntity<User> retrieveAllUserGet() {
        List<User> users = userRepository.findAllUser();

        if (users == null) {
            throw new UserNotFoundException(String.format("User not found"));
        }


        MappingJacksonValue mapping = new MappingJacksonValue(users);
        mapping.setFilters(filters);

        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("")
                        .buildAndExpand("")
                        .toUri()
        );

        return new ResponseEntity(mapping, header, HttpStatus.ACCEPTED);
    }

}
