package com.slack.slack.common.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoleHierarchy implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    private String child;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent", referencedColumnName = "child")
    private RoleHierarchy parent;

    @OneToMany(mappedBy = "parent")
    private List<RoleHierarchy> roleHierarchies = new ArrayList();
}
