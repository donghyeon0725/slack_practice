package com.slack.slack.appConfig.security.form.repository;

import com.slack.slack.appConfig.security.form.domain.Resources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResourcesRepository extends JpaRepository<Resources, Long> {

    @Query("select r from Resources r join fetch r.resourcesRoles where r.resourceType = 'URL' order by r.orderNum desc")
    List<Resources> findAllResources();
}
