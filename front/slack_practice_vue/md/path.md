📌 개발시 편의를 위한 절대경로 세팅
-
* 뷰에서는 "@"을 활용해서 절대 경로로 접근할 수 있다.
```javascript
import Demo from '@/assets/lasdw.js'
```

* 이는 node js의 (view 모듈의) 도움을 받아 사용하는 것이다. 
    1. jsconfig.json 파일 생성
    2. ```javascript
    {
      "compilerOptions": {
        "baseUrl": ".",
        "paths": {
          "~/*": [
            "./*"
          ],
          "@/*": [
            "./src/*"
          ]
        }
      },
      "exclude": [
        "node_modules",
        "dist"
      ]
    }
    ```
    

* 만약 인텔리제이에서 이 기능이 되지 않는 경우 아래와 같이 추가 세팅
    1. root 에 config.js 생성
    2. 아래 코드 붙여넣기
    ```javascript
    System.config({
        "paths": {
            "@/*": "./src/*"
        }
    });
    ```
    
