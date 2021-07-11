package com.slack.slack.appConfig.security.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourcesDTO {
    private String resourceName;

    private String resourceType;

    private Integer orderNum;

    private String resourcesRoles;
}
