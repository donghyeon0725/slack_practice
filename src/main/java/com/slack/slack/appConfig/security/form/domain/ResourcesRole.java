package com.slack.slack.appConfig.security.form.domain;

import com.slack.slack.appConfig.security.common.domain.Role;
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
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Resources resources;

    @ManyToOne(fetch = FetchType.LAZY)
    private Role role;



}
