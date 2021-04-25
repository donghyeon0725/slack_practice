package com.slack.slack.test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class testController {

    @GetMapping("filter_test/{id}")
    public String test_get(@PathVariable String id) {
        System.out.println(id);
        return "GetMapping";
    }

    @PostMapping("xss_test")
    public ResponseEntity test_post(@RequestBody XSS xss) {
        System.out.println(xss.getContent());
        System.out.println(xss.getTitle());
        return new ResponseEntity(xss, HttpStatus.OK);
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
