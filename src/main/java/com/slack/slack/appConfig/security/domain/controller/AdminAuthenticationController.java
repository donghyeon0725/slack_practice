package com.slack.slack.appConfig.security.domain.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminAuthenticationController {

    @Value("${admin.username}")
    private String input_id;

    @Value("${admin.password}")
    private String input_pw;

    @Value("${admin.loginProcessing}")
    private String input_login;

    @Value("${admin.csrf}")
    private String csrf;

    @Value("${admin.remember}")
    private String remember;

    // 관리자 로그인 페이지
    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        model.addAttribute("csrf_token",
                Optional.ofNullable(request)
                        .map(s -> s.getAttribute(CsrfToken.class.getName()))
                        .map(s -> ((CsrfToken)s).getToken())
                        .orElse("no token")
        );
        model.addAttribute("id", input_id);
        model.addAttribute("password", input_pw);
        model.addAttribute("login", input_login);
        model.addAttribute("csrf", csrf);
        model.addAttribute("remember", remember);

        return "admin/login/login";
    }

    // 관리자 로그인 실패
    @GetMapping("/failure")
    public String failure(HttpServletRequest request, Model model) {
        Optional opt_exception = Optional.ofNullable(request).map(req -> req.getParameter("exception"));
        model.addAttribute("exception", opt_exception.orElse(null));
        return "admin/login/failure";
    }

    // 관리자 인가 거부
    @GetMapping("/denied")
    public String denied(HttpServletRequest request, Model model) {
        Optional opt_exception = Optional.ofNullable(request).map(req -> req.getParameter("exception"));
        model.addAttribute("exception", opt_exception.orElse(null));
        return "admin/denied/denied";
    }

}
