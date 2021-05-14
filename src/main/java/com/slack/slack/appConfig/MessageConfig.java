package com.slack.slack.appConfig;

import net.rakugakibox.util.YamlResourceBundle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 다국어 지원을 위한 yml 로드 설정
 * */
@Configuration
public class MessageConfig {
    @Bean("messageSource")
    public MessageSource messageSource(
            @Value("${spring.messages.basename}") String basename,
            @Value("${spring.messages.encoding}") String encoding
    ) {

        YamlMessageSource ms = new YamlMessageSource();
        ms.setBasename(basename);
        ms.setDefaultEncoding(encoding);
        ms.setAlwaysUseMessageFormat(true);
        ms.setUseCodeAsDefaultMessage(true);
        ms.setFallbackToSystemLocale(true);
        return ms;
    }

    @Bean
    public LocaleResolver localResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        // 언어&국가정보가 없는 경우 미국으로 인식하도록 설정
        localeResolver.setDefaultLocale(Locale.US);
        return localeResolver;
    }

    class YamlMessageSource extends ResourceBundleMessageSource {
        @Override
        protected ResourceBundle doGetBundle(String basename, Locale locale) throws MissingResourceException {
            return ResourceBundle.getBundle(basename, locale, YamlResourceBundle.Control.INSTANCE);
        }
    }
}
