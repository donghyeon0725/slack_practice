package com.slack.slack.common.entity;

import lombok.*;

import javax.persistence.*;

/**
 * 다대다 관계를 풀어내기 위해서 엔티티 승격
 * */
@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountRole {

    @Id
    private Long accountRoleId;

    @JoinColumn(name = "account_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @JoinColumn(name = "role_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Role role;

}
