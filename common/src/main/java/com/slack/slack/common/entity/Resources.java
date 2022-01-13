package com.slack.slack.common.entity;

import com.slack.slack.common.code.ResourceType;
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
    private Long resourcesId;

    private String resourceName;

    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    private Long orderNum;

    public Resources(String resourceName, ResourceType resourceType, Long orderNum) {
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.orderNum = orderNum;
    }

    @OneToMany(mappedBy = "resources")
    private Set<ResourcesRole> resourcesRoles = new LinkedHashSet<>();

    public boolean isUrl() {
        return this.resourceType.equals(ResourceType.URL);
    }

    public boolean isMethod() {
        return this.resourceType.equals(ResourceType.METHOD);
    }

}
