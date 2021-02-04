# 1. Spring Boot Configuration Metadata

스프링 부트 어플리케이션을 개발할때, Configuration Properties 를 Java Beans 에 매핑하는 것은 매우 유용합니다.

많은 개발자들이 개발하는 과정에서 설정 파라미터 값을 변경해야 하는데, 이 기본 값이 무엇인지, 더 이상 사용되지 않는지

알기가 쉽지 않죠. 이러한 부분을 쉽게 JSON file 로 생성해주는 모듈입니다.



# 2. 의존성 추가

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter'
    compileOnly 'org.projectlombok:lombok'
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

프로젝트를 진행하면서 좀 더 쉽게 사용하기 위해서, 롬복같은 의존성들을 추가하였습니다.



> ​    annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'

configuration processor 를 사용하기 위해 의존성을 추가합니다.



위의 의존성 추가 과정을 거치지 말고 아래 코드를 다운받아서 시작해주세요.

> https://github.com/atlanboa/spring-boot-one-to-ten.git



# 3. Configuration Properties 예제

```java
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "database")
@Getter
@Setter
public class DatabaseProperties {

    @Getter
    @Setter
    public static class Server {

        private String ip;
        private int port;

    }

    private String username;
    private String password;
    private Server server;

}
```

processor 가 어떻게 동작하는지 보기 위해서, 스프링 부트 어플리케이션에서 사용되는 몇 가지 속성을 사용해보겠습니다.

## 3.1 @ConfigurationProperties 어노테이션

configuration processor 는 @ConfigurationProperties 를 가지고 있는 모든 클래스와 메소드를 스캔합니다.이를 통해서 configuration

parameter에 접근하고, configuration metadata 를 생성합니다.

## 3.2 속성 설정

```yaml
database:
  username: dev
  password: devpw
  server:
    ip: devip
    port: 1234
```

application.yml 파일에 아래와 같은 속성을 정의하겠습니다.

## 3.3 테스트

잘 읽어오는지 확인해보겠습니다.

```java
import com.plee.auth.AuthApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DatabasePropertiesIntegrationTest {

    @Autowired
    private DatabaseProperties databaseProperties;

    @Test
    public void whenSimplePropertyQueriedThenReturnsPropertyValue()
            throws Exception {
        Assertions.assertEquals("dev", databaseProperties.getUsername(),
                "Incorrectly bound Username property");
        Assertions.assertEquals("devpw", databaseProperties.getPassword(),
                "Incorrectly bound Password property");
    }

    @Test
    public void whenNestedPropertyQueriedThenReturnsPropertyValue()
            throws Exception {
        Assertions.assertEquals("devip", databaseProperties.getServer().getIp(),
                "Incorrectly bound Server IP nested property");
        Assertions.assertEquals(1234, databaseProperties.getServer().getPort(),
                "Incorrectly bound Server Port nested property");
    }
}
```

통합 테스트 코드는 테스트 소스 코드 패키지를 추가로 생성하고, 클래스 이름에 꼭 통합테스트라고 명시해주는게 좋습니다.

### 3.3.1 @ExtendWith

Junit5 에서 통합 테스트를 진행하기 위해서 사용하는 어노테이션입니다. SpringExtension.class 가 필요합니다.

### 3.3.2 @SpringBootTest

스프링 부트를 실행하면서 @Configuration 으로 지정된 클래스를 Bean 으로 등록해주는데, 이를 DI 받기 위해서는 필요합니다.

### 3.3.3 테스트

테스트는 성공적으로 됩니다.



# 4. application.yml 파일 Profile 분리

기본적으로 아무런 profile 설정을 하지 않으면, application.yml 파일을 찾아서 설정 값을 로드합니다.

하지만 우리가 개발을 진행할 때, 배포할 때는 사용하는 설정 값들이 달라지기 마련입니다.

기존의 파일을 세개로 분리해봅시다.

## 4.1 application.yml

```yaml
spring:
  profiles:
    active: dev
```

## 4.2 application-prod.yml

```yaml
database:
  username: prod
  password: prodpw
  server:
    ip: prodip
    port: 1234
```

## 4.3 application-dev.yml

```yaml
database:
  username: dev
  password: devpw
  server:
    ip: devip
    port: 1234
```



3개의 개발환경으로 나눴습니다.

4.1 과 같이 활성화할 프로파일을 dev 로 지정해주면 application-<profile>.yml 파일 중에서 profile 이 일치하는 파일을 사용하게 됩니다.



## 5. 분리한 application.yml 테스트

각각의 파일을 잘 로드하는지 테스트 하기 위해서 기존의 테스트 파일을 두개로 분리합니다.

### 5.1 DatabasePropertiesDevIntegrationTest.class

```java
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles(value = "dev")
public class DatabasePropertiesDevIntegrationTest {

    @Autowired
    private DatabaseProperties databaseProperties;

    @Test
    public void whenSimplePropertyQueriedThenReturnsPropertyValue()
            throws Exception {
        Assertions.assertEquals("dev", databaseProperties.getUsername(),
                "Incorrectly bound Username property");
        Assertions.assertEquals("devpw", databaseProperties.getPassword(),
                "Incorrectly bound Password property");
    }

    @Test
    public void whenNestedPropertyQueriedThenReturnsPropertyValue()
            throws Exception {
        Assertions.assertEquals("devip", databaseProperties.getServer().getIp(),
                "Incorrectly bound Server IP nested property");
        Assertions.assertEquals(1234, databaseProperties.getServer().getPort(),
                "Incorrectly bound Server Port nested property");
    }
}
```

### 5.2 DatabasePropertiesProdIntegrationTest.class

```java
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles(value = "prod")
public class DatabasePropertiesProdIntegrationTest {

    @Autowired
    private DatabaseProperties databaseProperties;

    @Test
    public void whenSimplePropertyQueriedThenReturnsPropertyValue()
            throws Exception {
        Assertions.assertEquals("prod", databaseProperties.getUsername(),
                "Incorrectly bound Username property");
        Assertions.assertEquals("prodpw", databaseProperties.getPassword(),
                "Incorrectly bound Password property");
    }

    @Test
    public void whenNestedPropertyQueriedThenReturnsPropertyValue()
            throws Exception {
        Assertions.assertEquals("prodip", databaseProperties.getServer().getIp(),
                "Incorrectly bound Server IP nested property");
        Assertions.assertEquals(1234, databaseProperties.getServer().getPort(),
                "Incorrectly bound Server Port nested property");
    }
}
```

### 5.3 @ActiveProfiles

통합 테스트를 진행할 때 필요한 설정 파일을 읽어오도록 지정할 수 있습니다.

#### @ActiveProfiles(value =  "prod")

application-prod.yml 파일을 읽어오도록 설정합니다.



# 6. 마무리

스프링 부트 설정값을 @Configuration 파일로 직접 읽어오는 방법과 @Configuration 파일을 테스트하는 방법을 확인했습니다.

완료한 코드는 아래에서 확인할 수 있습니다.

