package com.slack.slack.test;

import com.slack.slack.error.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
public class testController {

    @GetMapping("exception_test")
    public String test_get() {
        if (true) {
            throw new ResourceNotFoundException("에러를 일부러 내봄");
        }
        return "GetMapping";
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
