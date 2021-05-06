package com.slack.slack.mail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 외부로 부터 필요한 메일 정보를 가져오는 DTO
 * */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class MailDTO {
    // 받는 사람 이메일
    private String toAddress;

    // 제목
    private String subject;

    // 내용
    private String text;
}
