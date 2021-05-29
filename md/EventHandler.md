📌 이벤트 기반 프로그래밍을 위한 이벤트 핸들러
-
AOP 란. Aspect-Oriented Programming 의 약자로 관점 지향 프로그래밍이란 뜻이다.   
보안, 트랜젝션 등등 프로그래밍에서 시스템적인 관심사를 별도로 보고 기존의 비지니스 로직과 분리해서 처리를 하는 것을 의미한다.   
이벤트 기반 프로그래밍은 이런 관점 지향 프로그래밍의 일종이라고 할 수 있다.
 

이벤트 기반 프로그래밍과 가장 유사한 것은 에러 처리에 관련한 것이다. 

> 상황
* ip를 이용해서 회원가입하는 서비스가 있다고 가정하자. 이때 게시판을 하나 작성하는데, 오직 회원만 게시판을 작성할 수 있다.

위와 같은 상황에서 아래 코드를 작성할 수 있다. (아래 코드는 오직 예시일 뿐이다.)

```java
String ip = request.getParameter("ip");

userRepository.findByIpAddress(ip)
    .orElseThrow(() -> new UserNotFoundException("회원이 아닙니다."));

boardRepository.save(
    ... 중략
) 
```

회원이 아닌 사람이 글을 작성할 경우 UserNotFoundException 을 발생시킬 것이고, 
잘 작성된 서버 코드라면, 해당 에러를 처리하기 위한 에러 핸들러를 마련해두었을 것이다.

자! 여기서 힌트를 얻을 수 있다.
이벤트 기반 프로그래밍의 전체적인 흐름은 아래와 같다.
1. 이벤트 클래스 작성
2. 특정 상황에서 이벤트 발생 시키기(예외를 던지는 상황으로 가정하면 throw new UserNotFoundException 의 상황과 동일) (ApplicationEventPublisher 인터페이스를 implements 해야 하는데  ApplicationContext 클래스가 이미 상속하고 있으므로 그것을 사용해도 된다.)
3. 이벤트 핸들러 작성해서 이벤트 발생시 처리

자 이제 흐름은 알았으니, 이제 작성해보자!


<br/>

📌 예제 코드
- 
> 상황
* 파일 업로드 도중 에러가 발생 했을 때, 업로드 된 파일을 삭제 처리한다.

1. 파일 이벤트 클래스 작성
```java

/**
 * 파일 업로드 도중 에러가 발생했을 때, 파일을 처리를 위한 이벤트 클래스
 * */
public class FileEvent {
    private List<FileVO> files;

    public FileEvent(List<FileVO> files) {
        this.files = files;
    }

    public List<FileVO> getFiles() {
        return files;
    }

    public FileEvent setFiles(List<FileVO> files) {
        this.files = files;
        return this;
    }
}
```
파일 이벤트 처리를 위해, 이벤트 클래스 내부에 FileVO를 받아 둘 수 있도록 처리한다. 

2. 파일 이벤트 핸들러
```java
/**
 * 파일 이벤트 처리기
 * */
@Component
@RequiredArgsConstructor
public class FileEventHandler {

    private final FileManager fileManager;

    /**
     * 파일을 삭제합니다.
     * */
    @EventListener
    public void handle(FileEvent event) {
        List<FileVO> files = event.getFiles();

        if (files != null)
            fileManager.deleteFile(files);
    }
}
```
* @Component 어노테이션으로 ApplicationContext가 적절한 핸들러를 호출 할 수 있도록 빈에 등록한다.

3. ApplicationContext 을 통해 이벤트 발생시키기
```java
... 중략
} catch (RuntimeException e) {
    // 중간에 에러가 난 경우 이벤트를 발생시킵니다.
    applicationContext.publishEvent(new FileEvent(files));

    throw e;
}
```

