package com.slack.slack.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class testController {

    @GetMapping("test")
    public String test_get() {
        if (true) {
            throw new RuntimeException("에러");
        }

        return "GetMapping";
    }

    @PostMapping("test")
    public String test_post() {
        return "PostMapping";
    }

    @PutMapping("test")
    public String test_put() {
        return "PutMapping";
    }

    @DeleteMapping("test")
    public String test_delete() {
        return "DeleteMapping";
    }

}
