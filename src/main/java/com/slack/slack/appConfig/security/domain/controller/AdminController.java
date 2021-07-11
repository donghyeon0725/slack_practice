package com.slack.slack.appConfig.security.domain.controller;

import com.slack.slack.appConfig.security.domain.dto.ResourcesDTO;
import com.slack.slack.appConfig.security.domain.service.SecurityResourceService;
import com.slack.slack.appConfig.security.jwt.metadata.UrlFilterInvocationSecurityMetadataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    @Value("${admin.csrf}")
    private String csrf;

    private final SecurityResourceService securityResourceService;

    private final UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource;

    private void defaultFeild(HttpServletRequest request, Model model) {
        model.addAttribute("csrf", csrf);

        model.addAttribute("csrf_token",
                Optional.ofNullable(request)
                        .map(s -> s.getAttribute(CsrfToken.class.getName()))
                        .map(s -> ((CsrfToken)s).getToken())
                        .orElse("no token")
        );
    }


    @GetMapping("")
    public String main(Model model, HttpServletRequest request) {
        model.addAttribute("dashBoardPage", true);
        defaultFeild(request, model);

        return "admin/main";
    }

    @GetMapping("/url")
    public String url(Model model, HttpServletRequest request) {
        model.addAttribute("urlPage", true);
        defaultFeild(request, model);

        return "admin/main";
    }

    @PostMapping("/register")
    public void register(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ResourcesDTO resourcesDTO, Model model) throws IOException {
        securityResourceService.save(resourcesDTO);
        urlFilterInvocationSecurityMetadataSource.reload();

        model.addAttribute("dashBoardPage", true);
        defaultFeild(request, model);

        response.sendRedirect("/admin");
    }

    @GetMapping("/method")
    public String method(Model model, HttpServletRequest request) {
        model.addAttribute("methodPage", true);
        defaultFeild(request, model);

        return "admin/main";
    }

    @GetMapping("/role")
    public String role(Model model, HttpServletRequest request) {
        model.addAttribute("rolePage", true);
        defaultFeild(request, model);

        return "admin/main";
    }



}
