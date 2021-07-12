package com.slack.slack.appConfig.security.jwt.voter;

import com.slack.slack.appConfig.security.domain.service.SecurityResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class IpAddressVoter implements AccessDecisionVoter {

    private final SecurityResourceService securityResourceService;

    @Override
    public int vote(Authentication authentication, Object o, Collection collection) {
        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
        String remoteAddress = details.getRemoteAddress();

        List<String> accessIpList = securityResourceService.getAccessIpList();

        int result = ACCESS_DENIED;

        for (String ipAddress : accessIpList) {
            if (remoteAddress.equals(ipAddress)) {
                return ACCESS_ABSTAIN;
            }
        }

        if (result == ACCESS_DENIED)
            throw new AccessDeniedException("Invalid IpAddress");

        return result;
    }

    // ip 보터는 어떠한 경우에도 동작하도록 true 반환
    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class aClass) {
        return true;
    }
}
