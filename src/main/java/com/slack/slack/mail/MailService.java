package com.slack.slack.mail;

import com.slack.slack.domain.team.Team;
import com.slack.slack.error.exception.MailLoadFailException;
import com.slack.slack.system.Code;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public interface MailService {
    /* 비동기로 메일을 전송합니다. */
    CompletableFuture<Code> mailSendWithAsync(MailForm mailForm) throws RuntimeException;

    /* 동기로 메일을 전송합니다. */
    void mailSend(MailForm mailForm) throws RuntimeException;

    /* 회원가입 메일을 보냅니다. */
    void sendWelcomeMail(String email, Locale locale) throws MailLoadFailException;

    /* 초대 메일을 보냅니다. */
    void sendInviteMail(String from, String to, Team team, Locale locale) throws MailLoadFailException;
}
