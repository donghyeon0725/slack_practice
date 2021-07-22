package com.slack.slack;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController implements ErrorController {

    @GetMapping(value = "")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/error")
    public String handleError() {
        return "index";
    }

    @Override
    public String getErrorPath() {
        return null;
    }
}
