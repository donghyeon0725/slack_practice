package com.slack.admin;

import com.slack.slack.common.code.ResourceType;
import com.slack.slack.common.entity.*;
import com.slack.slack.common.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;

    private final RoleHierarchyRepository roleHierarchyRepository;

    private final ResourcesRepository resourcesRepository;

    private final ResourcesRoleRepository resourcesRoleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AccountRepository accountRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Role roleAdmin = new Role("ROLE_ADMIN");
        Role roleUser = new Role("ROLE_USER");

        roleRepository.save(roleAdmin);
        roleRepository.save(roleUser);

        RoleHierarchy root = RoleHierarchy.builder().child(roleAdmin.getRoleName()).build();
        RoleHierarchy child = RoleHierarchy.builder().parent(root).child(roleUser.getRoleName()).build();

        roleHierarchyRepository.save(root);
        roleHierarchyRepository.save(child);

        Resources resources1 = new Resources("/board", ResourceType.URL, 1L);
        Resources resources2 = new Resources("/board/**", ResourceType.URL, 2L);
        Resources resources3 = new Resources("/teams", ResourceType.URL, 3L);
        Resources resources4 = new Resources("/teams/**", ResourceType.URL, 4L);
        Resources resources5 = new Resources("/card", ResourceType.URL, 5L);
        Resources resources6 = new Resources("/card/**", ResourceType.URL, 6L);

        resourcesRepository.save(resources1);
        resourcesRepository.save(resources2);
        resourcesRepository.save(resources3);
        resourcesRepository.save(resources4);
        resourcesRepository.save(resources5);
        resourcesRepository.save(resources6);

        ResourcesRole resourcesRole1 = new ResourcesRole(resources1, roleUser);
        ResourcesRole resourcesRole2 = new ResourcesRole(resources2, roleUser);
        ResourcesRole resourcesRole3 = new ResourcesRole(resources3, roleUser);
        ResourcesRole resourcesRole4 = new ResourcesRole(resources4, roleUser);
        ResourcesRole resourcesRole5 = new ResourcesRole(resources5, roleUser);
        ResourcesRole resourcesRole6 = new ResourcesRole(resources6, roleUser);

        resourcesRoleRepository.save(resourcesRole1);
        resourcesRoleRepository.save(resourcesRole2);
        resourcesRoleRepository.save(resourcesRole3);
        resourcesRoleRepository.save(resourcesRole4);
        resourcesRoleRepository.save(resourcesRole5);
        resourcesRoleRepository.save(resourcesRole6);

        Account account = Account.builder().username("username").password(passwordEncoder.encode("password")).build();
        AccountRole accountRole = AccountRole.builder().role(roleAdmin).account(account).build();
        account.getAccountRoles().add(accountRole);
        accountRepository.save(account);

    }


//    @EventListener(ApplicationReadyEvent.class)
//    public void doSomethingAfterStartup() {
//    }
}
