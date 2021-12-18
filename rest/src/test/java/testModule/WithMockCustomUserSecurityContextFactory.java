package testModule;

import com.slack.slack.common.context.UserContext;
import com.slack.slack.common.entity.Role;
import com.slack.slack.common.entity.User;
import com.slack.slack.security.jwt.token.JwtAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;


import java.util.Arrays;

public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();


        JwtAuthenticationToken authentication =
                new JwtAuthenticationToken(
                        "token",
                        "secretKey",
                        customUser.username(),
                        null,
                        Arrays.asList(new SimpleGrantedAuthority(customUser.role())));

        context.setAuthentication(authentication);
        return context;
    }
}
