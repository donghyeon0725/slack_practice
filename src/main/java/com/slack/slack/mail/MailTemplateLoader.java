package com.slack.slack.mail;

/**
 * 메일 템플릿을 로드하는 클래스 입니다.
 * */
public interface MailTemplateLoader {
    String load(String file, Object model);
}
