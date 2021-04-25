


📌 Entity 생성
-
* @Entity 어노테이션을 붙여주면 Entity가 생성 됨

```java
@Entity
```


<br/>

📌 테이블 이름 지정하기
-
* @Table(name="log_api") 와 같이 사용

```java
@Table(name="log_api")
```


<br/>

📌 특정 필드가 외부에 노출 되는 것을 막고자 한다면
-

1. 필드에 주입하기

```java
@JsonIgnore
private String password;
```

2. 클래스 단에서 관리하기
```java
@JsonIgnoreProperties(value={"password", "ssn"})
public class User {
    private String password;
    private String ssn;
}
```

3. 상황에 따라 다르게 Filtering 하는 방법

* 필터를 적용할 클래스에 어노테이션과 함께 bean에 등록될 이름 정하기
```java
@JsonFilter("이름 A") 
// User 라는 클래스를 filtering 하고 싶으면 user에 위 어노테이션을 추가한다.
```

* 필터 생성 & FilterProvider 에 등록 (아까 등록한 이름을 대상으로 필터링 하겠다고 명시)
```java
// 간단한 필터 생성
SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "joinDate", "ssn");

// 위 필터를 우리가 사용 가능한 형태로 변경. UserInfo 을 대상으로 filter를 적용하겠다는 의미
FilterProvider filters = new SimpleFilterProvider().addFilter("UserInfo", filter);
```

* 필터링 된 객체를 return 
```java
MappingJacksonValue mapping = new MappingJacksonValue(user);
mapping.setFilters(filters);

return mapping;
```
List로 된 객체도 위와 동일하게 하면 적용 됨



<br/>

📌 PK 설정
-
```java
@Id
```



<br/>

📌 Auto Increament
-
```java
@GeneratedValue(strategy = GenerationType.IDENTITY)
```


<br/>


📌 DB와 Mapping 관계에서 제외하기
-
```java
@Transient
```


<br/>

📌 컬럼 크기 지정하기
-
```java
@Column(nullable = false, length = 100000)
```









