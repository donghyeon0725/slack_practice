package com.slack.slack.common.repository;

import com.slack.slack.common.entity.Resources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResourcesRepository extends JpaRepository<Resources, Long> {

    @Query("select r from Resources r join fetch r.resourcesRoles s join fetch s.role where r.resourceType = 'URL' order by r.orderNum desc")
    List<Resources> findAllResources();

    @Query("select r from Resources r join fetch r.resourcesRoles s join fetch s.role where r.resourceType = 'METHOD' order by r.orderNum desc")
    List<Resources> findAllMethodResources();

    @Query("select r from Resources r join fetch r.resourcesRoles s join fetch s.role where r.resourceType = 'POINTCUT' order by r.orderNum desc")
    List<Resources> findAllPointcutResources();
}
