package com.slack.slack.appConfig.security.form.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.slack.appConfig.security.common.domain.Role;
import com.slack.slack.appConfig.security.common.repository.RoleRepository;
import com.slack.slack.appConfig.security.form.domain.Resources;
import com.slack.slack.appConfig.security.form.domain.ResourcesDTO;
import com.slack.slack.appConfig.security.form.domain.ResourcesRole;
import com.slack.slack.appConfig.security.form.repository.ResourcesRepository;
import com.slack.slack.appConfig.security.form.repository.ResourcesRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.Provider;
import org.modelmapper.config.Configuration;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityResourceService {
    private final ResourcesRepository resourcesRepository;

    private final ResourcesRoleRepository resourcesRoleRepository;

    private final RoleRepository roleRepository;

    private final ModelMapper modelMapper;


    @Cacheable(value = "resourceList")
    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResourceList() {

        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> result = new LinkedHashMap<>();
        List<Resources> resourcesList = resourcesRepository.findAllResources();

        resourcesList.forEach(re ->
                {
                    List<ConfigAttribute> configAttributeList = new ArrayList<>();
                    re.getResourcesRoles().forEach(ro -> {
                        configAttributeList.add(new SecurityConfig(ro.getRole().getRoleName()));
                    });
                    result.put(new AntPathRequestMatcher(re.getResourceName()), configAttributeList);
                }
        );
        log.debug("cache test");
        return result;
    }

    @Transactional
    public Resources save(ResourcesDTO resourcesDTO) {

        Role role = roleRepository.findByRoleName(resourcesDTO.getResourcesRoles());
        Resources resources = modelMapper.map(resourcesDTO, Resources.class);
        resourcesRepository.save(resources);

        resourcesRoleRepository.save(
                ResourcesRole.builder()
                    .resources(resources)
                    .role(role)
                    .build()
        );

        return resources;
    }

}
