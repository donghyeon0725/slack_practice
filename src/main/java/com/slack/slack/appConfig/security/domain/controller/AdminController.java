package com.slack.slack.appConfig.security.domain.controller;

import com.slack.slack.appConfig.security.domain.dto.ResourcesDTO;
import com.slack.slack.appConfig.security.domain.dto.RoleDTO;
import com.slack.slack.appConfig.security.domain.entity.Resources;
import com.slack.slack.appConfig.security.domain.entity.ResourcesRole;
import com.slack.slack.appConfig.security.domain.entity.Role;
import com.slack.slack.appConfig.security.domain.hierarchy.SecurityRoleHierarchy;
import com.slack.slack.appConfig.security.domain.service.SecurityResourceService;
import com.slack.slack.appConfig.security.jwt.metadata.UrlFilterInvocationSecurityMetadataSource;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    @Value("${admin.csrf}")
    private String csrf;

    private final SecurityResourceService securityResourceService;

    private final UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource;

    private final ModelMapper mapper;

    private final RoleHierarchyImpl roleHierarchy;

    private void defaultFeild(HttpServletRequest request, Model model) {
        model.addAttribute("csrf", csrf);

        model.addAttribute("csrf_token",
                Optional.ofNullable(request)
                        .map(s -> s.getAttribute(CsrfToken.class.getName()))
                        .map(s -> ((CsrfToken)s).getToken())
                        .orElse("no token")
        );
    }


    @GetMapping("")
    public String main(Model model, HttpServletRequest request) {
        model.addAttribute("dashBoardPage", true);
        defaultFeild(request, model);

        model.addAttribute("resources", securityResourceService.getResources().stream().filter(Resources::isUrl).collect(Collectors.toList()));

        return "admin/main";
    }

    @GetMapping("/url")
    public String url_get(Model model, HttpServletRequest request) {
        model.addAttribute("urlPage", true);
        defaultFeild(request, model);

        model.addAttribute("resources", securityResourceService.getResources().stream().filter(Resources::isUrl).collect(Collectors.toList()));

        return "admin/main";
    }

    @GetMapping("/url/{id}")
    public String url_get(Model model, HttpServletRequest request, @PathVariable Long id) {
        model.addAttribute("urlDetailPage", true);
        defaultFeild(request, model);

        Resources resourceEntity = securityResourceService.getResource(id);
        Set<ResourcesRole> roleSet = resourceEntity.getResourcesRoles();
        List<Role> roleEntities = securityResourceService.getAllRoles();

        ResourcesDTO resource = mapper.map(resourceEntity, ResourcesDTO.class);
        List<RoleDTO> roles = new ArrayList<>();

        roleEntities.forEach(s -> {
            RoleDTO dto = mapper.map(s, RoleDTO.class);
            dto.setSystemHasThis(roleSet);
            roles.add(dto);
        });

        model.addAttribute("resource", resource);
        model.addAttribute("roles", roles);

        return "admin/main";
    }

    @PostMapping("/register")
    public void register(JpaTransactionManager tm, HttpServletRequest request, HttpServletResponse response, @ModelAttribute ResourcesDTO resourcesDTO, Model model) throws IOException {
        securityResourceService.save(resourcesDTO);
        urlFilterInvocationSecurityMetadataSource.reload();

        model.addAttribute("dashBoardPage", true);
        defaultFeild(request, model);

        response.sendRedirect("/admin");
    }

    @DeleteMapping("/delete")
    public void delete(HttpServletResponse response, @ModelAttribute ResourcesDTO resourcesDTO) throws IOException {
        securityResourceService.delete(resourcesDTO);

        response.sendRedirect("/admin");
    }

    @GetMapping("/method")
    public String method(Model model, HttpServletRequest request) {
        model.addAttribute("methodPage", true);
        defaultFeild(request, model);

        return "admin/main";
    }

    @GetMapping("/role")
    public String role_get(Model model, HttpServletRequest request) {
        model.addAttribute("rolePage", true);
        defaultFeild(request, model);

        model.addAttribute("roles", securityResourceService.getAllRoles().stream().map(s -> mapper.map(s, RoleDTO.class)).collect(Collectors.toList()));

        return "admin/main";
    }

    @GetMapping("/role/{id}")
    public String roleDetail_get(Model model, HttpServletRequest request, @PathVariable Long id) {
        model.addAttribute("roleDetailPage", true);
        model.addAttribute("role", mapper.map(securityResourceService.getRole(id), RoleDTO.class));
        defaultFeild(request, model);

        return "admin/main";
    }

    @GetMapping("/role/register")
    public String roleDetail_get(Model model, HttpServletRequest request) {
        model.addAttribute("roleDetailPage", true);
        defaultFeild(request, model);

        return "admin/main";
    }

    @PostMapping("/role/register")
    public void roleDetail_post(HttpServletResponse response, @ModelAttribute RoleDTO roleDTO) throws IOException {
        securityResourceService.save(roleDTO);
        ((SecurityRoleHierarchy)roleHierarchy).reload();

        response.sendRedirect("/admin");
    }

    @DeleteMapping("/role/delete")
    public void roleDetail_delete(HttpServletResponse response, @ModelAttribute RoleDTO roleDTO) throws IOException {
        securityResourceService.delete(roleDTO);

        response.sendRedirect("/admin");
    }


}
