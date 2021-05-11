package com.slack.slack.domain.user;

import com.slack.slack.error.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

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

    @PostMapping("/")
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

}
