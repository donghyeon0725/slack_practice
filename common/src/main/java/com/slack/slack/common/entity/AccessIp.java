package com.slack.slack.common.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccessIp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accessIpId;

    private String ipAddress;
}
