package com.slack.slack.mail;

import com.slack.slack.appConfig.encoding.Encoding;
import com.slack.slack.file.FileVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.slack.slack.system.Mode;

import static com.slack.slack.system.Mode.DEV;

/**
 * 메일을 보내기 위해서 시스템 적으로 해주어야 할 처리를 담당합니다.
 *
 *
 *   mail:
 *     host: smtp.gmail.com
 *     port: 587
 *     username: '<username/>'
 *     password: '<password/>'
 *     properties.mail.smtp:
 *       nickname: '<nickname/>'
 *       auth: true
 *       starttls.enable: true
 *       ssl.trust: smtp.gmail.com
 *
 *  위 내용을 설정 파일에 추가함으로써, mailSender가 추가된다.
 *
 *  그런데 이것은 AutoWired 어노테이션으로 추가되는 것이기 때문에
 *  컴파일 단계에서 에러를 찾을 수 없고
 *  @Bean으로 관리를 해줘야 생성자로 추가된다.
 *
 *  메일을 설정하는 부분인 MailConfig를 살펴보면 위와 관련해서 설정이 있다.
 * */
@Component
public class MailManager {
    private final JavaMailSender sender;
    private MimeMessage message;
    private MimeMessageHelper messageHelper;
    private final MailTemplateLoader mailTemplateLoader;
    @Value("${spring.profiles.active}")
    private String env_mode;


    // 생성자
    protected MailManager(JavaMailSender sender, MailTemplateLoader mailTemplateLoader) throws MessagingException {
        this.sender = sender;
        message = sender.createMimeMessage();
        messageHelper = new MimeMessageHelper(message, true, Encoding.UTF8.get());
        this.mailTemplateLoader = mailTemplateLoader;
    }

    protected JavaMailSender setForm(MailForm form) throws MessagingException, IOException {
        // 보내는 사람 이름
        messageHelper.setFrom(form.getFromAddress(), form.getFromName());
        // 받는 사람 이메일
        messageHelper.setTo(form.getToAddress());
        // 제목
        messageHelper.setSubject(form.getSubject());

        // html 텍스트인 경우
        if (form.isHtmlText()) {
            // 폼을 불러옵니다.
            String text = mailTemplateLoader.load(form.getTemplatePath(), form.getModel());
            messageHelper.setText(text, form.isHtmlText());
        } else {
            messageHelper.setText(form.getText());
        }

        // 첨부파일
        Optional<List<FileVO>> opt_attachments = Optional.ofNullable(form.getAttachments());

        opt_attachments.ifPresent(attachments -> {
            if (attachments.size() > 0) {
                try {
                    for (int i=0; i<attachments.size(); i++) {
                        File file = null;

                        // 배포 환경에서 사용할 소스
                        if (env_mode.equalsIgnoreCase(DEV.name())) {
                            file = new File(attachments.get(i).getAbsolutePath());
                        } else {
                            file = new ClassPathResource(attachments.get(i).getAbsolutePath()).getFile();
                        }

                        FileSystemResource fsr = new FileSystemResource(file);

                        // 파일의 실제 이름으로 첨부
                        messageHelper.addAttachment(attachments.get(i).getFileName(), fsr);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });


        // 이미지
        Optional<List<FileVO>> opt_mailImages = Optional.ofNullable(form.getMailImages());

        opt_mailImages.ifPresent(mailImages -> {
            if (mailImages.size() > 0) {
                try {
                    for (int i = 0; i < mailImages.size(); i++) {
                        // 배포 환경에서 사용할
                        File file = null;
                        if (env_mode.equalsIgnoreCase(DEV.name())) {
                            file = new File(mailImages.get(i).getAbsolutePath());
                        } else {
                            file = new ClassPathResource(mailImages.get(i).getPath()).getFile();
                        }

                        FileSystemResource fsr = new FileSystemResource(file);

                        // 시스템 이름으로 아이디 설정
                        messageHelper.addInline(mailImages.get(i).getSystemName(), fsr);;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });


        return this.sender;
    }


    // 발송
    protected void send() {
        try {
            sender.send(message);

        } catch (MailAuthenticationException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("계정 인증 실패");
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
