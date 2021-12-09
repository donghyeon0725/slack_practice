package com.slack.slack.domain.service.impl;

import com.slack.slack.common.dto.RoleDTO;
import com.slack.slack.common.entity.Role;
import com.slack.slack.common.repository.AccessIpRepository;
import com.slack.slack.common.repository.RoleRepository;
import com.slack.slack.common.entity.Resources;
import com.slack.slack.common.dto.ResourcesDTO;
import com.slack.slack.common.entity.ResourcesRole;
import com.slack.slack.common.repository.ResourcesRepository;
import com.slack.slack.common.repository.ResourcesRoleRepository;
import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityResourceService {
    private final ResourcesRepository resourcesRepository;

    private final ResourcesRoleRepository resourcesRoleRepository;

    private final RoleRepository roleRepository;

    private final ModelMapper modelMapper;

    private final AccessIpRepository accessIpRepository;


    @PersistenceContext
    private EntityManager entityManager;


    @Cacheable(value = "resourceList")
    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResourceList() {

        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> result = new LinkedHashMap<>();
        List<Resources> resourcesList = resourcesRepository.findAllResources();

        getResourceMap(result, resourcesList);
        return result;
    }

    private void getResourceMap(LinkedHashMap<RequestMatcher, List<ConfigAttribute>> result, List<Resources> resourcesList) {
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
    }

    private void getResourceStringMap(LinkedHashMap<String, List<ConfigAttribute>> result, List<Resources> resourcesList) {
        resourcesList.forEach(re ->
                {
                    List<ConfigAttribute> configAttributeList = new ArrayList<>();
                    re.getResourcesRoles().forEach(ro -> {
                        configAttributeList.add(new SecurityConfig(ro.getRole().getRoleName()));
                    });
                    result.put(re.getResourceName(), configAttributeList);
                }
        );
        log.debug("cache test");
    }

    @Cacheable(value = "methodResourceList")
    public LinkedHashMap<String, List<ConfigAttribute>> getMethodResourceList() {

        LinkedHashMap<String, List<ConfigAttribute>> result = new LinkedHashMap<>();
        List<Resources> resourcesList = resourcesRepository.findAllMethodResources();

        getResourceStringMap(result, resourcesList);
        return result;
    }

    @Cacheable(value = "pointcutResourceList")
    public LinkedHashMap<String, List<ConfigAttribute>> getPointcutResourceList() {

        LinkedHashMap<String, List<ConfigAttribute>> result = new LinkedHashMap<>();
        List<Resources> resourcesList = resourcesRepository.findAllPointcutResources();

        getResourceStringMap(result, resourcesList);
        return result;
    }

    public List<Resources> getResources() {
        return resourcesRepository.findAllResources();
    }

    public Resources getResource(Long id) {
        return resourcesRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional
    public Resources save(ResourcesDTO resourcesDTO) {

        Role role = roleRepository.findByRoleName(resourcesDTO.getResourcesRoles().get(0));
        Resources resources = modelMapper.map(resourcesDTO, Resources.class);
        resourcesRepository.save(resources);

        resourcesRoleRepository.deleteByResources(resources);

        if (resourcesRoleRepository.findByResourcesAndRole(resources, role) == null)
            resourcesRoleRepository.save(
                    ResourcesRole.builder()
                        .resources(resources)
                        .role(role)
                        .build()
            );

        entityManager.flush();
        entityManager.clear();
        return resources;
    }

    @Transactional
    public void delete(ResourcesDTO resourcesDTO) {
        Resources resources = modelMapper.map(resourcesDTO, Resources.class);
        resourcesRoleRepository.deleteByResources(resources);
        resourcesRepository.deleteById(resourcesDTO.getId());

        entityManager.flush();
        entityManager.clear();
    }


    public Role getRole(Long id) {
        return roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Transactional
    public Role save(RoleDTO roleDTO) {
        Role role = roleRepository.save(modelMapper.map(roleDTO, Role.class));
        entityManager.flush();
        entityManager.clear();

        return role;
    }

    @Transactional
    public void delete(RoleDTO roleDTO) {
        roleRepository.deleteById(roleDTO.getId());
        entityManager.flush();
        entityManager.clear();
    }


    /**
     * 모든 ip 리스트를 가져온다.
     * */
    @Cacheable(value = "accessIpList")
    public List<String> getAccessIpList() {

        List<String> accessIpList = accessIpRepository.findAll().stream().map(accessIp -> accessIp.getIpAddress()).collect(Collectors.toList());

        return accessIpList;
    }
}
