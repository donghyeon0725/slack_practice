๐ ํ๋ ํํฐ๋งํ๊ธฐ
-

* ํ๋์ ํํฐ๋งํ๊ธฐ
```java
@JsonIgnore
private String password;
@JsonIgnore
private String ssn;
```

* ํด๋์ค ๋จ์์ ํํฐ๋ง ํ๊ธฐ
```java
@JsonIgnoreProperties(value={"password", "ssn"})
public class User {
    private String password;
    private String ssn;
}
```


* ์ํฉ์ ๋ฐ๋ผ ๋ค๋ฅด๊ฒ ํํฐ๋งํ๊ธฐ

1. ํํฐ๋ฅผ ์ ์ฉํ  ํด๋์ค์ ์ด๋ธํ์ด์๊ณผ ํจ๊ป bean์ ๋ฑ๋ก๋  ์ด๋ฆ ์ ํ๊ธฐ
```java
// User ๋ผ๋ ํด๋์ค๋ฅผ filtering ํ๊ณ  ์ถ์ผ๋ฉด user์ ์ ์ด๋ธํ์ด์์ ์ถ๊ฐํ๋ค.
@JsonFilter("์ด๋ฆ A") 
```

2.  ํํฐ ์์ฑ & FilterProvider ์ ๋ฑ๋ก (์๊น ๋ฑ๋กํ ์ด๋ฆ์ ๋์์ผ๋ก ํํฐ๋ง ํ๊ฒ ๋ค๊ณ  ๋ช์)
```java
// ํํฐ ์์ฑ
SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "joinDate", "ssn");

// ์ ํํฐ๋ฅผ ์ฐ๋ฆฌ๊ฐ ์ฌ์ฉ ๊ฐ๋ฅํ ํํ๋ก ๋ณ๊ฒฝ. UserInfo ์ ๋์์ผ๋ก filter๋ฅผ ์ ์ฉํ๊ฒ ๋ค๋ ์๋ฏธ
FilterProvider filters = new SimpleFilterProvider().addFilter("UserInfo", filter);
```

3. ํํฐ๋ง ๋ ๊ฐ์ฒด๋ฅผ return (ResponseEntity๋ฅผ ์ฌ์ฉํ๋ ๊ฒฝ์ฐ body๋ก ์ธํ)
```java
MappingJacksonValue mapping = new MappingJacksonValue(user);
mapping.setFilters(filters);

return mapping;
```

์ด๋ xml ํ์์ผ๋ก ์์ฒญํด๋ ๋๊ฐ์ด ์ ์ฉ ๋๋ค.








