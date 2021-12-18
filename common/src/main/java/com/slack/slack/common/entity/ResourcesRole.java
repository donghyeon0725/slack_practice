package com.slack.slack.common.entity;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Setter
@Builder
public class ResourcesRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resourceRoleId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Resources resources;

    @ManyToOne(fetch = FetchType.LAZY)
    private Role role;

    public ResourcesRole(Resources resources, Role role) {
        this.resources = resources;
        this.role = role;
    }
}
