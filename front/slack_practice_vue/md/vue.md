📌 vue 프로젝트 생성하기
-

[뷰 공식 문서](https://cli.vuejs.org/) 에 들어가서 installation 카테고리를 확인해보면

* 뷰 명령어 도구 설치 (cli)
```text
npm install -g @vue/cli
```
위 명령어를 사용하면 자동으로 최신 버전의 뷰 cli 가 설치된다.
만약 vue cli가 설치되지 않는다면 -g (글로벌 옵션)과 연관 되어 있을 가능성이 크다.
글로벌 사용할 때 설치를 수행하는 컴퓨터의 관리자는 root 유저가 아닌 유저를 사용할 수 있는데, 이럴때 설치가 안될 수 있다. 그럴 때 아래와 같은 명령어로 사용자를 변경하자

```text
npm -g config set user root
```

* 뷰 명령어로 SPA(single page application) 생성
```text
vue create vue-til (프로젝트 이름)
```
위 명령어를 사용하면 preset을 선택해야 하는데 preset은 플러그인의 집합이다. 선택은 아래와 같이 한다.

* Manually select features => 선택 설치 하겠다는 말임
* Babel, Linter, Unit (Router와 Vuex는 일단 선택 x. 스페이스바로 선택)
* 2.x 버전 선택
* ESLint + Prettier
* Lint on save
* Jest
* In dedicated config files (전용 설정 파일을 사용하면 여러가지 장점이 있다)







