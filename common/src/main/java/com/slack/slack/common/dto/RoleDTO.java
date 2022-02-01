package com.slack.slack.common.dto;

import com.slack.slack.common.entity.ResourcesRole;
import com.slack.slack.common.entity.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class RoleDTO {
    @Id
    private Long roleId;

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
