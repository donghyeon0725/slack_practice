package com.slack.slack.mail;

import com.slack.slack.error.exception.ErrorCode;
import com.slack.slack.error.exception.MailLoadFailException;
import com.slack.slack.system.Code;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static com.slack.slack.system.Code.COMPLETE;
import static com.slack.slack.system.Code.FAIL;

/**
 * 메일링 서비스에서 비지니스 로직을 담당할 클래스
 * */
@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {
    private final MailManager mailManager;

    @Value("${spring.mail.username}")
    private String fromAddress;
    @Value("${spring.mail.properties.mail.smtp.nickname}")
    private String fromName;

    @Async(value = "mailSenderExecutor")
    public CompletableFuture<Code> mailSendWithAsync(MailForm mailForm) throws RuntimeException {
        try {
            send(mailForm);
        } catch (Exception e) {
            throw new MailLoadFailException(FAIL.getDescription());
        }

        return CompletableFuture.completedFuture(COMPLETE);
    }

    public void mailSend(MailForm mailForm) throws RuntimeException {
        send(mailForm);
    }

    private void send(MailForm mailForm) {
        mailForm.setFromAddress(fromAddress);
        mailForm.setFromName(fromName);

        try {
            mailManager.setForm(mailForm);
            mailManager.send();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
