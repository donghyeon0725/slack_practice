메일 보내기
-

0. 디팬던시 추가
```html
<!-- 메일 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```
1. 메일 정보를 외부에서 받아올 [MailDTO](../src/main/java/com/slack/slack/mail/MailDTO.java)
2. 메일 정보를 주고 받을 [MailForm](../src/main/java/com/slack/slack/mail/MailForm.java)
3. 메일의 기본값을 설정해주고, 기본 사용법을 정의할 [MailService](../src/main/java/com/slack/slack/mail/MailService.java)
4. 메일의 시스템 적인 부분을 설정할 [MailManager](../src/main/java/com/slack/slack/mail/MailManager.java)
5. 메일의 템플릿을 불러올 [MailTemplateLoaderImpl](../src/main/java/com/slack/slack/mail/MailTemplateLoaderImpl.java)


<br/>


📌 에러관련
-

```html
 send failed, exception: com.sun.mail.smtp.SMTPSendFailedException: 530 5.7.0 Must issue a STARTTLS command first. nv2sm4478384pbb.6
```
이 때 위와 같은 에러가 나는 경우가 있는데 원인은 아래와 같다. 


인증되지 않은 연결을 통해 제 3 자에게 메일을 전송하기 위해 포트 25에서 Gmail 서버를 사용하려고 할 수 있습니다. Gmail에서는이 작업을 허용하지 않습니다. 
그러면 누구나 Gmail 서버를 사용하여 다른 사람에게 메일을 보낼 수 있기 때문 입니다. 이를 오픈 릴레이 라고하며 초기에는 스팸의 일반적인 원인이었습니다. 오픈 릴레이는 더 이상 인터넷에서 허용되지 않습니다.

SMTP 클라이언트에 인증 된 연결 (아마도 포트 587) 을 사용하여 Gmail에 연결하도록 요청해야합니다 .

출처 : <https://stackoverflow.com/questions/10509699/must-issue-a-starttls-command-first>

그런데 587 포트를 사용하려면 
smtp 권한이나, enable 여부 등등 설정해주어야 할 것들이 있고 해당 설정을 true로 해주지 않으면 에러가 납니다.

해당 설정 내용은 [MailConfig.java](../src/main/java/com/slack/slack/appConfig/MailConfig.java) 의 mailSender 메소드를 보면 알 수 있습니다.



📌 메일 엔진으로서 머스타치
-
[머스타치](Mustache.md)
