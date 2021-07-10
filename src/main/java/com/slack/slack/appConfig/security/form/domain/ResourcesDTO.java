package com.slack.slack.appConfig.security.form.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class ResourcesDTO {
    private String resourceName;

    private String resourceType;

    private Integer orderNum;

    private String resourcesRoles;
}
