package com.slack.slack.common.util;

import org.modelmapper.ModelMapper;

public class ModelMapperUtils {
    private static ModelMapper modelMapper;

    public static ModelMapper getModelMapper() {
        return modelMapper;
    }
}
