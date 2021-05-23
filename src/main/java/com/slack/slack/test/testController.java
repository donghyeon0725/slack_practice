package com.slack.slack.test;

import com.slack.slack.appConfig.prometheus.Prometheus;
import com.slack.slack.error.exception.ErrorCode;
import com.slack.slack.error.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class testController {

    private final Prometheus prometheus;

    @GetMapping("/test")
    public void test() {
        prometheus.getCounter().increment();
    }

    @GetMapping("exception_test")
    public String test_get() {
        if (true) {
            throw new ResourceNotFoundException(ErrorCode.INVALID_INPUT_VALUE);
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
