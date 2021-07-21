📌 nvm 이란?
-
Node Version Manager의 약자
노드의 경우, 개발 환경에서 여러 버전이 필요할 수 있는데,
노드 버전의 스위칭을 쉽게 할 수 있도록 도와줌.
현재 프로젝트에서 노드는 10.16.3


<br/>

📌 nvm 설치
-
<https://github.com/nvm-sh/nvm> 링크에서 설치 가능함

nvm-windows 링크인 <https://github.com/coreybutler/nvm-windows>에 들어가

download Now 하기 <https://github.com/coreybutler/nvm-windows/releases>

nvm-setup.zip 파일을 다운로드 해서 설치하면 된다.

<br/>

📌 nvm 설정
-
윈도우가 아닌 bash shell 등을 사용하는 경우 별도의 설정이 필요한데
아래와 같은 설정을 해주면 된다

* ~/.bashrc 열어서 아래와 같이 수정
```text
export NVM_DIR="$([ -z "${XDG_CONFIG_HOME-}" ] && printf %s "${HOME}/.nvm" || printf %s "${XDG_CONFIG_HOME}/nvm")"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh" # This loads nvm
```


<br/>

📌 nvm 버전 확인 
-
```text
nvm --version
```

<br/>

📌 node 버전 변경
-

* 설치
```text
nvm install <version>
nvm install 10.16.3 
```

* 버전 변경
```text
nvm use 10.16.3
```

* 노드 버전 확인
```text
node -v
```



<br/>

📌 node 를 통한 의존성 다운로드
-

* 프로젝트 폴더로 이동
```text
cd /mnt
cd c
cd Users
cd <사용자이름>
cd Desktop
```

* 의존성 설치
```text
npm i
npm install
```

