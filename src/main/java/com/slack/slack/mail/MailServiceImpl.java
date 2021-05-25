package com.slack.slack.mail;

import com.slack.slack.appConfig.security.TokenManager;
import com.slack.slack.domain.team.Team;
import com.slack.slack.error.exception.ErrorCode;
import com.slack.slack.error.exception.InvalidInputException;
import com.slack.slack.error.exception.MailLoadFailException;
import com.slack.slack.system.Code;
import com.slack.slack.system.Key;
import com.slack.slack.system.Time;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static com.slack.slack.system.Code.COMPLETE;
import static com.slack.slack.system.Code.FAIL;

/**
 * 메일링 서비스에서 비지니스 로직을 담당할 클래스
 * 서비스를 호출하는 컨트롤러는, 시스템적인 동작만 할 것이기 때문에
 * 서비스에서 환영메일을 보내는 메소드를 작성할 때는
 * 이메일만 받고, 모델은 서비스 측에서 해결해야할 것 같다.
 * */
@Service
@Slf4j
@RequiredArgsConstructor
public class MailServiceImpl implements MailService{

    @Value("${spring.mail.username}")
    private String fromAddress;
    @Value("${spring.mail.properties.mail.smtp.nickname}")
    private String fromName;

    /* 다국어 */
    private final MessageSource messageSource;

    /* token 관리자 */
    private final TokenManager tokenManager;

    /* mail 관리자 */
    private final MailManager mailManager;

    public CompletableFuture<Code> mailSendWithAsync(MailForm mailForm) throws RuntimeException {
        try {
            send(mailForm);
        } catch (Exception e) {
            throw new MailLoadFailException(ErrorCode.UNEXPECTED_SERVER_ACTION);
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

    /**
     * 환영 메일을 보냅니다.
     * */
    @Async(value = "mailSenderExecutor")
    @Override
    public void sendWelcomeMail(String email, Locale locale) throws MailLoadFailException, InvalidInputException {
        if (!MailUtil.isValidEmail(email)) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);
        }

        String token = tokenManager.createToken(Key.JOIN_KEY, Time.FIVE_MINUTE, Arrays.asList(email));
        String subject = messageSource.getMessage("email.welcome", null, locale);

        Map model = new HashMap();
        model.put("email", email);
        model.put("token", token);

        MailForm mailForm = MailForm.builder()
                .templatePath("welcome.mustache")
                .htmlText(true)
                .model(model)
                .subject(subject)
                .toAddress(email)
                .build();

        this.mailSendWithAsync(mailForm)
                .thenAcceptAsync((s) -> {
                    try {
                        // 성공시 파일 삭제
                        // fileManager.deleteFile(list);
                        System.out.println(s.getDescription());
                    } catch (MailLoadFailException e) {
                        throw new MailLoadFailException(e.getErrorCode());
                    }
                })
                .exceptionally(e -> {
                    System.out.println(e.getMessage());
                    return null;
                });
    }

    /**
     * 초대 메일을 보냅니다.
     * */
    @Async(value = "mailSenderExecutor")
    @Override
    public void sendInviteMail(String from, String to, Team team, Locale locale) throws MailLoadFailException, InvalidInputException {
        if (!MailUtil.isValidEmail(to)) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);
        }

        String token = tokenManager.createToken(Key.INVITE_KEY, Time.ONE_DAY, Arrays.asList(from, team.getId().toString()));
        String subject = from.concat(messageSource.getMessage("email.invite", null, locale));

        Map model = new HashMap();
        model.put("email", to);
        model.put("token", token);
        model.put("title", team.getName());
        model.put("description", team.getDescription());

        MailForm mailForm = MailForm.builder()
                .templatePath("invite.mustache")
                .htmlText(true)
                .model(model)
                .subject(subject)
                .toAddress(to)
                .build();

        this.mailSendWithAsync(mailForm)
                .thenAcceptAsync((s) -> {
                    System.out.println(s.getDescription());
                })
                .exceptionally( e -> {
                    System.out.println(e.getMessage());
                    return null;
                });
    }


}
