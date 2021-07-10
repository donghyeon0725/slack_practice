package com.slack.slack.appConfig.security.jwt.metadata;

import com.slack.slack.appConfig.security.form.service.SecurityResourceService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.LinkedHashMap;
import java.util.List;

public class UrlResourcesMapFactoryBean implements FactoryBean<LinkedHashMap<RequestMatcher, List<ConfigAttribute>>> {

    /**
     * DB 로 부터 가지고 온 데이터 매핑
     * */
    private SecurityResourceService securityResourceService;

    /**
     * Bean 으로 만들 객체
     * */
    private LinkedHashMap<RequestMatcher, List<ConfigAttribute>> resourcesMap;

    public void setSecurityResourceService(SecurityResourceService securityResourceService) {
        this.securityResourceService = securityResourceService;
    }

    /**
     * resourcesMap 가 없는 경우 DB에서 가져와 반환을 하도록 만듬 => SingleTon 으로 되어 있는 이유는, 메모리에 오직 하나만 생성하도록 만들기 위함이다.
     * */
    public void init() {
        resourcesMap = securityResourceService.getResourceList();
    }

    /**
     * resourcesMap 을 반환
     * */
    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getObject() {
        /**
         * 이미 값이 있는 경우 DB에 가지 않는다.
         * */
        if (resourcesMap == null) {
            init();
        }
        return resourcesMap;
    }

    public Class<LinkedHashMap> getObjectType() {
        return LinkedHashMap.class;
    }

    public boolean isSingleton() {
        return true;
    }
}
