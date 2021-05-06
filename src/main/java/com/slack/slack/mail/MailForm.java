package com.slack.slack.mail;

import com.slack.slack.file.FileVO;
import lombok.*;

import java.util.Arrays;
import java.util.List;

/**
 * 메일을 보내기 위해 필요한 정보를 모두 포함합니다.
 *
 * 아래 클래스를 통해서 메일을 보내기 위한 기본 폼을 작성합니다.
 * */
@Data
// 빌더 패턴은 전체 필드에 대한 생성자가 존재해야하만 함
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailForm {

    // 보내는 사람 이메일
    private String fromAddress;
    // 보내는 사람 이름
    private String fromName;

    // 받는 사람 이메일
    @NonNull
    private String toAddress;

    // 제목
    private String subject;

    // 내용
    private String text;

    // 첨부 파일이 있는 경우
    private List<FileVO> attachments;

    // 이미지가 삽입된 경우
    private List<FileVO> mailImages;

    // html 여부
    @NonNull
    private boolean htmlText;


    // 템플릿 경로
    private String templatePath;

    // 템플릿에 전달할 오브젝트
    private Object model;

}
