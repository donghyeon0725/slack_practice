package com.slack.slack.test;

import com.slack.slack.file.FileManager;
import com.slack.slack.file.FileVO;
import com.slack.slack.mail.MailDTO;
import com.slack.slack.mail.MailForm;
import com.slack.slack.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 파일 업로드 테스트를 위한 컨트롤러
 * */
@Slf4j
@Controller
public class ViewController {
//
//    @Autowired
//    private FileManager fileManager;
//
//    @Autowired
//    private MailService mailService;
//
//    @GetMapping("mail")
//    public String mail_get() {
//        return "mail";
//    }
//
//
//    @PostMapping("mail")
//    public void mail_post(HttpServletRequest request, MailDTO mail) {
//        // 파일 업로드
//        List<FileVO> list = fileManager.fileUpload(request);
//
//        for (FileVO temp : list) {
//            System.out.println(temp.getFileName());
//        }
//
//        MailForm mailForm = MailForm.builder()
//                .htmlText(false)
//                .subject(mail.getSubject())
//                .text(mail.getText())
//                .toAddress(mail.getToAddress())
//                .attachments(Arrays.asList(list.get(0)))
//                .mailImages(Arrays.asList(list.get(1)))
//                .build();
//
//        // 비동기 처리를 위한
//        mailService.mailSendWithAsync(mailForm)
//                .thenAcceptAsync(
//                    (result) -> {
//                        log.debug(result.toString());
//                        fileManager.deleteFile(list);
//                    }
//                )
//                .exceptionally((e) -> {
//                        log.debug(e.getMessage());
//                        return null;
//                    }
//                );
//    }
//
//    public class mailModel {
//        private String message = "임시로 메세지를 생성했습니다.";
//        public String getMessage() {
//            return this.message;
//        }
//    }
//
//    @PostMapping("mailwithform")
//    public String mail_with_form_post(HttpServletRequest request, MailDTO mail) {
//        // 파일 업로드
//        List<FileVO> list = fileManager.fileUpload(request);
//
//        for (FileVO temp : list) {
//            System.out.println(temp.getFileName());
//        }
//
//        mailModel model = new mailModel();
//
//
//        MailForm mailForm = MailForm.builder()
//                .templatePath("mail.mustache")
//                .htmlText(true)
//                .model(model)
//                .subject(mail.getSubject())
//                .toAddress(mail.getToAddress())
//                .attachments(Arrays.asList(list.get(0)))
//                .mailImages(Arrays.asList(list.get(1)))
//                .build();
//
//        // 비동기 처리를 위한
//        mailService.mailSendWithAsync(mailForm)
//                .thenAcceptAsync(
//                        (result) -> {
//                            log.debug(result.toString());
//                            fileManager.deleteFile(list);
//                        }
//                )
//                .exceptionally((e) -> {
//                            log.debug(e.getMessage());
//                            return null;
//                        }
//                );
//
//        return "mail";
//    }
//
//    @GetMapping("file")
//    public String file_test() {
//        return "file";
//    }
//
//    @PostMapping("file")
//    public String file_upload(HttpServletRequest request) {
//        List<FileVO> list = fileManager.fileUpload(request);
//
//        for (FileVO file : list) {
//            System.out.println(file.getPath());
//            System.out.println(file.getFileName());
//            System.out.println(file.getExt());
//            System.out.println(file.getSystemName());
//        }
//
//        boolean result = fileManager.deleteFile(list);
//        System.out.println("result : " + result);
//
//        return "file";
//    }
//
//
//    @GetMapping("email")
//    public String mail_get(HttpServletRequest request, Model model) {
//        model.addAttribute("email", "ehdgus5015@gmail.com");
//        model.addAttribute("token", "testetst");
//        return "mailTemplates/welcome";
//    }
//
//    @GetMapping("join/{email}/{token}")
//    public String join_get(@PathVariable String email, @PathVariable String token, Model model) {
//        model.addAttribute("email", email);
//        model.addAttribute("token", token);
//
//        return "views/join";
//    }
//
//
//

}
