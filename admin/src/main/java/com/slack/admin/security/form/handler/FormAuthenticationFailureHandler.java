package com.slack.admin.security.form.handler;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FormAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private String failureUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        Assert.notNull(failureUrl, "Authentication Failure URL Could Not Found");

        String errorMsg = "";

        // AuthenticationException 을 통해서 예외를 받을 수 있기 때문에 이것으로 상황에 따라서 다양한 예외 처리를 할 수 있다.
        if (exception instanceof BadCredentialsException)
            errorMsg = "Invalid Username or Password";
        else if (exception instanceof UsernameNotFoundException)
            errorMsg = "Username does not found";


        // 가는 url을 지정해주기 위해서 setDefaultFailureUrl 메소드를 호출했다.
        setDefaultFailureUrl(failureUrl.concat("?exception=" + errorMsg));
        // redirect 하는 부분이 없을 때에는 onAuthenticationFailure 호출
        super.onAuthenticationFailure(request, response, exception);
    }

    public FormAuthenticationFailureHandler setFailureUrl(String failureUrl) {
        this.failureUrl = failureUrl;
        return this;
    }
}
