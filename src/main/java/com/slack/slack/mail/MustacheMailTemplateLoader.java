package com.slack.slack.mail;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mustache.MustacheResourceTemplateLoader;
import org.springframework.stereotype.Component;

import java.io.Reader;

/**
 * 머스타치 템플릿을 로드하기 위한 클래스
 * */
@Component
public class MustacheMailTemplateLoader implements MailTemplateLoader {
    @Value("${custom.mailtemplate}")
    private String MAIL_PATH;

    public String load(String file, Object model) {
        try {
            MustacheResourceTemplateLoader mustacheResourceTemplateLoader = new MustacheResourceTemplateLoader();

            Reader reader = mustacheResourceTemplateLoader.getTemplate(MAIL_PATH.concat(file));
            Template template = Mustache.compiler().compile(reader);

            return template.execute(model);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}


