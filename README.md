# inventory - 재고 관리 API 시스템

### 프로젝트 요약
- Spring boot + MySQL + Redis 를 사용해 Thread-Safe를 구현했습니다.
- Redisson 라이브러리를 사용합니다.


### redisson을 채택한 이유
동시성 이슈를 풀어나가려면 낙관적락, 비관적락을 이해해야합니다.

어느 정도의 트래픽은 mysql도 감당을 하지만 redis가 성능면은 더 뛰어납니다.

그래서 이번 프로젝트에는 Redis를 채택했습니다.

Redis로 확정하고 나서도 Lettuce 와 Redisson 사이에서 고민을 했습니다. 

둘의 가장 큰 차이는 재시도를 하는지의 여부입니다.

재고관리 시스템 상 재고가 0인 상태면 재시도를 해도 의미가 없다는 생각에 Redisson을 채택하게 되었습니다.

## 목차
1. [📋 개발 환경](#개발-환경)   

2. [⚙필수 설치 및 사전 설정](#필수-설치-및-사전-설정)
    - [JDK 설치하기](#jdk설치하기)
    - [MySQL 설치하기](#mysql-설치하기)
    - [Redis 설치하기](#redis-설치하기)

3. [💻 빌드 방법](#빌드-방법)

4. [💻 실행 방법](#실행-방법)

5. [💡 사용 방법](#사용-방법)   

7. [✔ 테스트 방법](#사용-방법)   

8. [✒ 후기](#프로젝트-후기)

9. [🔗 Notion 상세 정리 바로가기](https://carnation-harrier-bf6.notion.site/4c70ae6318a84b319286c51a96ee93f0)


## 개발 환경
✔ JDK 17.0.2

✔ MySQL 8.0.31

✔ Redis 3.0.504

✔ Spring Boot 2.7.6

✔ Gradle 7.4 

✔ Persistence Framework JPA

## 프로젝트 설치 및 실행 방법
### JDK 설치하기
##### jdk-17.0.2 다운로드 후 JAVA_HOME 환경 변수 설정은 필수입니다.
```
# wget https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_linux-x64_bin.tar.gz
```

---
### MySQL 설치하기
##### rpm 파일 다운로드
```
# wget https://dev.mysql.com/get/mysql80-community-release-el7-7.noarch.rpm
```

##### rpm 설치
```
# sudo rpm -ivh mysql80-community-release-el7-7.noarch.rpm
```

##### server 설치
```
# yum -y install mysql-community-server
```

##### MySQL시작
```
# systemctl start mysqld
```


##### 사용자 계정과 스키마는 아래 정보로 반드시 생성해줘야 합니다.
- id: test 
- pw: testTEST1234! 
- schema : inventory

---

### Redis 설치하기
##### EPEL Repository 설치
```
# sudo yum install epel-release yum-utils
```

##### redis 설치
```
# sudo yum install redis
```

## 빌드 방법
###### Build가 완료된 Jar 파일은 (/…../build/libs) 경로에 생성됩니다.
###### Window cmd로 빌드하는 방법과 IntelliJ에서 빌드하는 방법을 소개합니다.
---
#### **window 환경(cmd)에서 빌드 방법**

##### >> gradlew 명령어를 통해서 jar 파일을 생성합니다.
```
# gradlew bootjar
```
##### >> 혹시나 빌드에 실패한다면 clean 명령어를 실행해주고 다시 build 합니다.
```
# gradlew clean
# gradlew clean build
```
---
#### **IDE(IntelliJ 기준) 빌드 방법**

###### >> view > Tool Windows > Gradle
![image](https://user-images.githubusercontent.com/48276424/211705441-dac3d518-d9e4-40a9-b6df-1fad2f973fd9.png)
###### >> Gradle > build > bootJar
![image](https://user-images.githubusercontent.com/48276424/211705484-ce01a994-8b84-4828-bcaa-075e89cbe975.png)

## 실행 방법

###### 서버에 띄워진 내용을 테스트하려면 8080 방화벽 포트를 열어야하니 방화벽 먼저 열어줍니다.
```
# firewall-cmd --permanent --zone=public --add-port=8080/tcp
```
---
###### 방화벽 설정을 추가하고 나면 꼭 방화벽을 reload 해줘야합니다.
```
# firewall-cmd --reload
```
---
###### 이제 준비가 끝났습니다. 생성된 jar 파일을 실행하고 싶은 서버 위치로 옮긴 후 jar를 실행합니다. 
###### jar 실행은 nohup 명령어를 통해 진행합니다.
###### 앞에 nohup을 붙이고 뒤에 '&' 를 붙이면 백그라운드 실행이 되어 터미널을 종료해도 계속 동작합니다. 
```
# nohup java -jar /home/inventory-0.0.1-SNAPSHOT.jar --spring.profiles.active=local &
```


## 사용 방법
###### 기초 데이터 세팅을 위해 application-local.yaml에 자동생성 옵션을 넣고 있습니다.
###### 테스트용 프로젝트여서 create-drop으로 프로세스 시작 시점에 더미데이터를 생성하고, 종료시점에 테이블을 Drop하고 있습니다. 
###### 원치 않으실 경우 application-local.yaml에 있는 jpa.hibernate.ddl-auto 내용을 none으로 변경하시기 바랍니다.
###### ddl-auto 설정을 none으로 해두면 hibernate의 자동 생성 옵션이 동작하지 않습니다.
![image](https://user-images.githubusercontent.com/48276424/211721053-4231bcd9-5ddf-4329-877a-41b8d0e0fb75.png)


## 테스트 방법

##### 테스트코드는 IDE에서 JUnit 코드를 실행하여 확인이 가능합니다.
##### 프로젝트 build 시에 따로 Ignore를 설정 하지 않았으므로 Build가 잘 되었다면 테스트도 성공한 것입니다.
![image](https://user-images.githubusercontent.com/48276424/211842920-850b6e46-bec4-4c1a-b0b4-c1cdf72be9d6.png)
---

###### Spring Boot를 띄우고 PostMan으로도 간단하게 테스트가 가능합니다.
![image](https://user-images.githubusercontent.com/48276424/211756239-09e2df1a-5c7d-413b-8129-bad2eec8c1f9.png)


## 프로젝트를 마치며
여태는 synchronized 로 많이 처리를 해왔는데 안전하게 막아주지만 속도가 저하되는 단점이 있었습니다.
최근들어 동시성 문제에 대해 고민을 해왔었는데 redisson을 적용한 기능을 구현 할 수 있어서 좋은 경험이였다고 생각합니다. 멀티스레드 테스트를 구성하고 포트가 다른 boot 두 개를 띄워 놓고 하나의 DB로 접근하여 테스트를 했는데 병렬적으로 실행되었지만 잔여 재고 개수는 정확했습니다. 앞으로 같이 일하는 동료가 동시성 문제를 얘기한다면 redis를 이용한 분산락 처리에 대한 의견을 내 볼 수 있을거 같습니다.
추후에 실무에서도 redisson 혹은 Lettuce를 적용해보고 싶다는 생각이 들었고, 다음에는 Lettuce를 활용한 시스템도 구성해보고 싶습니다.
