📌 package.json 파일
-
* package.json 파일은 개발시, 또는 배포시 사용할 라이브러리(디팬던시)를 모아 놓은 곳이다.


<br/>


📌 devDependencies vs dependencies 차이점
-
* devDependencies는 개발할 때 사용할 모듈이다. 여기에는 문법 검사기인 prettier 등도 포함되어 있다. 
* 허나 운영 환경에서 prettier는 필요가 없다. 이를 구분하기 위해서 devDependencies를 별도로 마련해 둔 것이다.

```text
npm run build
```
* 위 명령어는 배포환경에서 사용할 라이브러리를 뽑아내는 명령어인데, 그 때 뽑아질 라이브러리가 dependencies에 명시된 라이브러리다.


