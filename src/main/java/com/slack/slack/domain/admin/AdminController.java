package com.slack.slack.domain.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Value("${admin.csrf}")
    private String csrf;


    @GetMapping("")
    public String main(Model model, HttpServletRequest request) {
        model.addAttribute("csrf", csrf);

        model.addAttribute("csrf_token",
                Optional.ofNullable(request)
                .map(s -> s.getAttribute(CsrfToken.class.getName()))
                .map(s -> ((CsrfToken)s).getToken())
                .orElse("no token")
        );

        return "admin/main";
    }

    @GetMapping("/sample")
    public String sample() {
        return "admin/sample";
    }

}
