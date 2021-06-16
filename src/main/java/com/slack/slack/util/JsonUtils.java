package com.slack.slack.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;

public final class JsonUtils {

  private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

  private JsonUtils() {
  }

  public static String toJson(Object object) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to convert object to JSON string", e);
    }
  }

  public static <T> T toObject(String json, Class<T> clazz) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(json, clazz);
    } catch (IOException e) {
      log.error("Failed to convert string `" + json + "` class `" + clazz.getName() + "`", e);
      return null;
    }
  }

  public static void write(Writer writer, Object value) throws IOException {
    new ObjectMapper().writeValue(writer, value);
  }


  /**
   * 소켓 통신을 할 때 데이터를 filtering 하기 위해서 사용합니다.
   * */
  public static ObjectMapper objectMapperSettingFilter(FilterProvider filters) {
    ObjectMapper om = new ObjectMapper();
    return om.setFilterProvider(filters);
  }

  /**
   * pojo to objectmapper
   * */
  public static ObjectMapper objectMapper() {
    return new ObjectMapper();
  }


}
