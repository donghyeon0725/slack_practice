package com.slack.slack.requestmanager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.MappingJacksonValue;

public class ResponseFilterManager {
    private static final Logger log = LoggerFactory.getLogger(ResponseFilterManager.class);

    /**
     * Response 에 body를 붙이는 용도로 사용합니다.
     * */
    public static MappingJacksonValue setFilters(Object target, FilterProvider filters) {
        MappingJacksonValue mapping = new MappingJacksonValue(target);
        mapping.setFilters(filters);

        return mapping;
    }
}
