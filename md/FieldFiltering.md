📌 필드 필터링하기
-

* 필드에 필터링하기
```java
@JsonIgnore
private String password;
@JsonIgnore
private String ssn;
```

* 클래스 단에서 필터링 하기
```java
@JsonIgnoreProperties(value={"password", "ssn"})
public class User {
    private String password;
    private String ssn;
}
```


* 상황에 따라 다르게 필터링하기

1. 필터를 적용할 클래스에 어노테이션과 함께 bean에 등록될 이름 정하기
```java
// User 라는 클래스를 filtering 하고 싶으면 user에 위 어노테이션을 추가한다.
@JsonFilter("이름 A") 
```

2.  필터 생성 & FilterProvider 에 등록 (아까 등록한 이름을 대상으로 필터링 하겠다고 명시)
```java
// 필터 생성
SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "joinDate", "ssn");

// 위 필터를 우리가 사용 가능한 형태로 변경. UserInfo 을 대상으로 filter를 적용하겠다는 의미
FilterProvider filters = new SimpleFilterProvider().addFilter("UserInfo", filter);
```

3. 필터링 된 객체를 return (ResponseEntity를 사용하는 경우 body로 세팅)
```java
MappingJacksonValue mapping = new MappingJacksonValue(user);
mapping.setFilters(filters);

return mapping;
```

이는 xml 형식으로 요청해도 똑같이 적용 된다.








