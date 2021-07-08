package com.slack.slack.domain.user;

import com.slack.slack.appConfig.security.common.domain.Role;
import com.slack.slack.appConfig.security.form.domain.Account;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Role role;
}
