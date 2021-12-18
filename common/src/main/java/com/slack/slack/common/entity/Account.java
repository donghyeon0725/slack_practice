package com.slack.slack.common.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    private Long accountId;

    private String username;

    private String password;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private Set<AccountRole> accountRoles = new HashSet<>();

}
