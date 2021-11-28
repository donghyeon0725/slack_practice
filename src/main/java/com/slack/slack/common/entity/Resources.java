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
    private Long id;

    private String resourceName;

    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    private Long orderNum;

    @OneToMany(mappedBy = "resources")
    private Set<ResourcesRole> resourcesRoles = new LinkedHashSet<>();

    public boolean isUrl() {
        return this.resourceType.equals(ResourceType.URL);
    }

    public boolean isMethod() {
        return this.resourceType.equals(ResourceType.METHOD);
    }

}
