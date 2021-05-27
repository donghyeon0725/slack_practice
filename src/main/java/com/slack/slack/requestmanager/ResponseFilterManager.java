package com.slack.slack.requestmanager;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import org.springframework.http.converter.json.MappingJacksonValue;

public class ResponseFilterManager {
    public static Object setFilters(Object target, FilterProvider filters) {
        MappingJacksonValue mapping = new MappingJacksonValue(target);
        mapping.setFilters(filters);

        return mapping;
    }
}
