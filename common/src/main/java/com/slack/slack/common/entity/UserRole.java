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
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userRoleId;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Role role;
}
