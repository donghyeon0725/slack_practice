📌 프론트 엔드 api의 문서화
-
* jsDoc 문법을 사용해서 api 문서화를 진행하면 됨


<br/>

📌 jsDoc 공식 문서
-
* <https://jsdoc.app/about-getting-started.html>


<br/>


📌 jsDoc 간단 시작하기
-
* 아래 코드와 같은 형태로 문서화 한다.
```javascript
/**
 * Represents a book.
 * @constructor
 * @param {string} title - The title of the book.
 * @param {string} author - The author of the book.
 */
function Book(title, author) {
}
```
* jsdoc 파일을 담을 폴더 생성
```text
mkdir doc-js
cd doc-js
```
* jsDoc을 생성하려는 폴더 아래에서 초기화를 진행한다.
```text
npm init -y
```

* index.js 파일 생성 (이 때 index.js 는 문서화 할 파일이다.)
```text
touch index.js
```


* jsDoc 설치
```text
// cmd
npm i --save-dev jsdoc
```

* 테마 설치 (선택)
```text
// cmd
npm install docdash
```

* 작성한 주석을, 문서로 만들기
```text
npm install -g jsdoc
jsdoc ./index.js 
```



