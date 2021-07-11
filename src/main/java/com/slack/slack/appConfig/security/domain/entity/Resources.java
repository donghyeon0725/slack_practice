package com.slack.slack.appConfig.security.domain.entity;

import com.slack.slack.appConfig.security.domain.common.ResourceType;
import lombok.*;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
public class Resources {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String resourceName;

    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    private Long orderNum;

    @OneToMany(mappedBy = "resources")
    private Set<ResourcesRole> resourcesRoles = new LinkedHashSet<>();

}
