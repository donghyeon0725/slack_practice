


๐ Entity ์์ฑ
-
* @Entity ์ด๋ธํ์ด์์ ๋ถ์ฌ์ฃผ๋ฉด Entity๊ฐ ์์ฑ ๋จ

```java
@Entity
```


<br/>

๐ ํ์ด๋ธ ์ด๋ฆ ์ง์ ํ๊ธฐ
-
* @Table(name="log_api") ์ ๊ฐ์ด ์ฌ์ฉ

```java
@Table(name="log_api")
```


<br/>

๐ ํน์  ํ๋๊ฐ ์ธ๋ถ์ ๋ธ์ถ ๋๋ ๊ฒ์ ๋ง๊ณ ์ ํ๋ค๋ฉด
-

1. ํ๋์ ์ฃผ์ํ๊ธฐ

```java
@JsonIgnore
private String password;
```

2. ํด๋์ค ๋จ์์ ๊ด๋ฆฌํ๊ธฐ
```java
@JsonIgnoreProperties(value={"password", "ssn"})
public class User {
    private String password;
    private String ssn;
}
```

3. ์ํฉ์ ๋ฐ๋ผ ๋ค๋ฅด๊ฒ Filtering ํ๋ ๋ฐฉ๋ฒ

* ํํฐ๋ฅผ ์ ์ฉํ  ํด๋์ค์ ์ด๋ธํ์ด์๊ณผ ํจ๊ป bean์ ๋ฑ๋ก๋  ์ด๋ฆ ์ ํ๊ธฐ
```java
@JsonFilter("์ด๋ฆ A") 
// User ๋ผ๋ ํด๋์ค๋ฅผ filtering ํ๊ณ  ์ถ์ผ๋ฉด user์ ์ ์ด๋ธํ์ด์์ ์ถ๊ฐํ๋ค.
```

* ํํฐ ์์ฑ & FilterProvider ์ ๋ฑ๋ก (์๊น ๋ฑ๋กํ ์ด๋ฆ์ ๋์์ผ๋ก ํํฐ๋ง ํ๊ฒ ๋ค๊ณ  ๋ช์)
```java
// ๊ฐ๋จํ ํํฐ ์์ฑ
SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "joinDate", "ssn");

// ์ ํํฐ๋ฅผ ์ฐ๋ฆฌ๊ฐ ์ฌ์ฉ ๊ฐ๋ฅํ ํํ๋ก ๋ณ๊ฒฝ. UserInfo ์ ๋์์ผ๋ก filter๋ฅผ ์ ์ฉํ๊ฒ ๋ค๋ ์๋ฏธ
FilterProvider filters = new SimpleFilterProvider().addFilter("UserInfo", filter);
```

* ํํฐ๋ง ๋ ๊ฐ์ฒด๋ฅผ return 
```java
MappingJacksonValue mapping = new MappingJacksonValue(user);
mapping.setFilters(filters);

return mapping;
```
List๋ก ๋ ๊ฐ์ฒด๋ ์์ ๋์ผํ๊ฒ ํ๋ฉด ์ ์ฉ ๋จ



<br/>

๐ PK ์ค์ 
-
```java
@Id
```



<br/>

๐ Auto Increament
-
```java
@GeneratedValue(strategy = GenerationType.IDENTITY)
```


<br/>


๐ DB์ Mapping ๊ด๊ณ์์ ์ ์ธํ๊ธฐ
-
```java
@Transient
```


<br/>

๐ ์ปฌ๋ผ ํฌ๊ธฐ ์ง์ ํ๊ธฐ
-
```java
@Column(nullable = false, length = 100000)
```




<br/>

๐ ๊ธฐ๋ณธ ๋ฐ์ดํฐ๋ฅผ ๋ฃ์ด ๋๊ณ  ์ถ๋ค๋ฉด
-

1. data.sql ์์ฑ
2. insert into ... 
