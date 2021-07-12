package com.slack.slack.appConfig.security.domain.dto;

import com.slack.slack.appConfig.security.domain.entity.ResourcesRole;
import com.slack.slack.appConfig.security.domain.entity.Role;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import java.util.Set;

@Getter
@Setter
public class RoleDTO {
    @Id
    private Long id;

    private String roleName;

    private boolean has;

    public boolean isSystemHasThis() {
        return this.has;
    }

    public RoleDTO setSystemHasThis(Set<ResourcesRole> roles) {

        for (ResourcesRole r : roles) {
            if (r.getRole().equals(
                    Role.builder()
                    .roleName(this.roleName)
                    .build())) {
                this.has = true;
                return this;
            }

        }
        this.has = false;

        return this;
    }
}
