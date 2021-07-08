package com.slack.slack.appConfig.security.jwt.service;

import com.slack.slack.appConfig.security.jwt.domain.UserContext;
import com.slack.slack.domain.user.UserRepository;
import com.slack.slack.error.exception.ErrorCode;
import com.slack.slack.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserContext(userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.RESOURCE_NOT_FOUND.getMessage())));
    }
}
