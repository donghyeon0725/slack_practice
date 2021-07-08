package com.slack.slack.appConfig.security.form.details;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

public class FormWebAuthenticationDetails extends WebAuthenticationDetails {
    public FormWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
    }
}
