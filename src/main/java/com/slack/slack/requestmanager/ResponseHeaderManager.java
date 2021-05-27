package com.slack.slack.requestmanager;

import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class ResponseHeaderManager {
    public static HttpHeaders headerWithOnePath(String value) {
        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{value}")
                        .buildAndExpand(value)
                        .toUri()
        );

        return header;
    }

    public static HttpHeaders headerWithOnePath(Integer value) {
        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{value}")
                        .buildAndExpand(value)
                        .toUri()
        );

        return header;
    }

    public static HttpHeaders headerWithThisPath() {
        HttpHeaders header = new HttpHeaders();
        header.setLocation(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("")
                        .buildAndExpand()
                        .toUri()
        );

        return header;
    }
}
