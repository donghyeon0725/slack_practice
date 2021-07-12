package com.slack.slack.appConfig.security.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResourcesDTO {
    private Long id;

    private String resourceName;

    private String resourceType;

    private Integer orderNum;

    private List<String> resourcesRoles;
}
