이전 포스팅에서는 서비스 레이어에 관해서 다뤘습니다.

이번 레이어는 컨트롤러 레이어입니다.



# 1. UserController

이 레이어에서는 모든 서비스 메소드를 다 활용하지는 않겠습니다.

먼저 코드를 보기전에 개념적인 부분만 짚고 갈게요.

## 1.1 @Controller vs @RestController

그냥 @Controller 와 @RestController 의 차이점이 무엇인가에 대해서 잠깐 짚고 갈 필요가 있습니다.

### 1.1.1 @Controller

전통적인 Spring MVC의 컨트롤러인 @Controller 는 주로 ***View 를 반환***하기 위해 사용됩니다.

![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2F2BnED%2Fbtqybg36Dak%2F3HgL3gUKHBSOmyeM4hIn00%2Fimg.png)

기본적인 MVC flow 를 확인해보겠습니다.



1. 사용자가 요청을 보낸다.
2. Dispatcher Servlet 이 URL 과 매핑되는 컨트롤러를 리턴한다.
3. 해당되는 컨트롤러는 요청을 처리하고, ModelAndView 를 리턴한다.



여기서 3번이 컨트롤러의 역할이 되는거죠. 또한 이때 View 를 반환하기 위해 **ViewResolver** 가 사용됩니다.

정확하게는 **컨트롤러가 Dispatcher Servlet 에 나 이러한 View 를 반환할게** 라고 요청하면

Dispatcher Servlet 에서 찾아서 리턴해줍니다.



또한 Spring MVC 에서 컨트롤러 또한 데이터를 반환해야 되는 경우도 있습니다. 이 때는 반환 타입에 쉽게

@ResponseBody 를 붙여주면 됩니다.

```java
package com.mang.blog.application.user.controller;

import com.mang.blog.application.user.model.UserVO;
import com.mang.blog.application.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/user")
public class UserController {

    @Resource(name = "userService")
    private UserService userService;

    @PostMapping(value = "/info")
    public @ResponseBody User info(@RequestBody User user){
        return userService.retrieveUserInfo(user);
    }
    
    @GetMapping(value = "/infoView")
    public String infoView(Model model, @RequestParam(value = "userName", required = true) String userName){
        User user = userService.retrieveUserInfo(userName);
        model.addAttribute("user", user);
        return "/user/userInfoView";
    }

}
```

그럼 @RestController 가 왜 필요한 것인가요.



### 1.1.2 @RestController

주로 데이터를 반환하기 위한 목적으로 사용됩니다. REST 한 방식으로 설계하는 API 에서 주로 사용됩니다.

기존의 컨트롤러에서 데이터를 반환하기 위해서는 @ResponseBody 가 필요하다고 했는데, @RestController 는 기본적으로 반환에

@ResponseBody 가 붙어있습니다. Json 형식의 데이터를 쉽게 반환할 수 있죠.



> @Controller와 @RestController의 차이 https://mangkyu.tistory.com/49

## 1.2 UserController Class

```java
package com.plee.auth.controller;

import com.plee.auth.domain.User;
import com.plee.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/email")
    public User findUserByEmail(@RequestParam String email) {
        return userService.findByEmail(email);
    }

    @GetMapping("/user/id")
    public ResponseEntity<User> findUserById(@RequestParam Long id) {
        return ResponseEntity.ok(userService.get(id));
    }
}
```

기존의 Controller 클래스와는 다르게  RestController에서는 따로 ResponseEntity 가 필요하지 않습니다.

하지만 여기서 알고 넘어가야 되는 부분은 2가지입니다.

### 1.2.1 ResponseEntity

단순히 findUserByEmail 처럼 엔티티를 반환해버리면 User 클래스가 Json 으로 변경된 값만 넘어갑니다.

일반적으로는 **데이터를 반환할 때 상태 코드와 함께 반환**해주는 것이 좋습니다.

따라서 findUserById 처럼 상태 코드를 함께 반환해주는 것이죠.

### 1.2.1 User Entity

우리는 User 클래스를 엔티티 클래스로 사용하고 있습니다.

하지만 이렇게 엔티티 클래스를 Data Transfer Object 즉 Dto 로 사용하게 되면 몇가지 문제점이 있습니다.

1. 개발 단계에서 요구 사항에 따라 Client 가 요구하는 데이터 형식이 달라질 가능성이 매우 높다.
2. 데이터 형식이 변경되면 데이터베이스까지 영향을 미친다.



따라서, 이렇게 직접 엔티티 클래스를 반환하는 것보다 컨트롤러와 클라이언트 사이의 통신을 위한 Dto 를 따로 정의하는게 좋습니다.

위의 코드를 수정해보겠습니다.



```java
package com.plee.auth.controller;

import com.plee.auth.domain.User;
import com.plee.auth.dto.UserDto;
import com.plee.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/email")
    public ResponseEntity<UserDto> findUserByEmail(@RequestParam String email) {
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(null).build());
    }

    @GetMapping("/user/id")
    public ResponseEntity<UserDto> findUserById(@RequestParam Long id) {
        User user = userService.get(id);
        return ResponseEntity.ok(UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(null)
                .build());
    }

    @PostMapping("/user")
    public ResponseEntity<UserDto> addUser(@RequestBody UserDto userDto) {
        User user = userService.add(User.builder()
                .id(null)
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .build());
        return ResponseEntity.ok(UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(null)
                .build());
    }
}

```



```java
import com.plee.auth.domain.User;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String password;

    private UserDto(Long id, String email) {
        this.id = id;
        this.email = email;
        this.password = null;
    }

    public static UserDto of(User user) {
        return new UserDto(user.getId(), user.getEmail());
    }
}

```

위와 같이 수정하면, 더 이상 Controller Layer 에서 엔티티 클래스를 직접 반환하지 않게 됩니다.

물론 User와 관련된 데이터를 전송받을 때도 UserDto 를 통해서 받으면 됩니다.

또한 데이터 전송에 있어서 매번 비밀번호가 노출되는 것을 올바르지 않음으로, 관련 요청이 있을때만 따로 사용할 수 있도록

password 필드는 항상 Null 로 초기화합니다.



추가적으로 유저를 추가하는 요청도 하나 만들어줍니다.



자 여기까지 코드가 완성됐으면, 이제 직접 컨트롤러 Unit 테스트를 만들어보겠습니다.

여기서는 post 에 대한 부분은 넘어가겠습니다.

# 2. Controll Unit Test

```java
import com.plee.auth.domain.User;
import com.plee.auth.dto.UserDto;
import com.plee.auth.exception.UserNotFoundException;
import com.plee.auth.service.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserControllerUnitTest {

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private UserController userController;


    @Test
    void findUserByEmailSuccessTest() throws Exception {

        final User testUser = new User(0L, "test@gmail.com", "password");
        final UserDto testUserDto = UserDto.of(testUser);

        given(userService.findByEmail(testUserDto.getEmail()))
                .willReturn(testUser);

        ResponseEntity<UserDto> responseEntity = userController.findUserByEmail(testUserDto.getEmail());
        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        Assertions.assertEquals(testUserDto.getEmail(), Objects.requireNonNull(responseEntity.getBody()).getEmail());
        verify(userService).findByEmail(anyString());

    }

    @Test
    void findUserByEmailFailureTest() throws Exception {

        given(userService.findByEmail(anyString()))
                .willThrow(UserNotFoundException.class);

        Assertions.assertThrows(UserNotFoundException.class, () -> userController.findUserByEmail(anyString()));
        verify(userService).findByEmail(anyString());

    }

    @Test
    void findUserByIdSuccessTest() {
        final User testUser = new User(0L, "test@gmail.com", "password");
        final UserDto testUserDto = UserDto.of(testUser);

        given(userService.get(testUser.getId()))
                .willReturn(testUser);

        ResponseEntity<UserDto> responseEntity = userController.findUserById(testUser.getId());
        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        Assertions.assertEquals(testUserDto.getEmail(), Objects.requireNonNull(responseEntity.getBody()).getEmail());
        verify(userService).get(anyLong());
    }

    @Test
    void findUserByIdFailureTest() {
        given(userService.get(anyLong()))
                .willThrow(UserNotFoundException.class);
        Assertions.assertThrows(UserNotFoundException.class, () -> userController.findUserById(anyLong()));
        verify(userService).get(anyLong());
    }
}
```

컨트롤러 유닛 테스트 또한 다른 의존성에 영향을 받지 않도록, 메소드에 대한 로직을 테스트하는 것입니다.

따라서 Mock 객체를 사용할 수 있도록 @ExtendWith(MockitoExtension.class) 를 사용합니다.

UserController 는 UserService에 의존성을 가지고 있습니다. 또한 UserServiceImpl 을 직접 사용하게 됩니다.

따라서 여기서는 컨트롤러가 런타임에서 주입받을 UserServiceImpl 을 Mock 객체로 생성해주고

@InjectMocks 로 userController 를 선언합니다.



따라서 각 메소드들을 호출되었을때, 적절한 응답 코드와 반환값들을 넘겨주는지 테스트합니다.



# 3. Controller MockMvc Test

MockMvc 테스트는 실질적으로 API의 엔드포인트에 대한 요청을 테스트할 수 있습니다.

제가 이 부분을 공부하면서, 크게 삽질한 부분이 있는데 그 부분도 짚고 넘어가겠습니다.

먼저 코드입니다.

```java
package com.plee.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plee.auth.domain.User;
import com.plee.auth.dto.UserDto;
import com.plee.auth.service.UserServiceImpl;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ActiveProfiles("dev")
public class UserControllerMockMvcTest {


    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private UserServiceImpl userService;

    final UserDto userDto = UserDto.builder()
            .id(3L)
            .email("email")
            .password("password")
            .build();

    final User user = User.builder()
            .id(3L)
            .email("email")
            .password("password")
            .build();

    @Test
    public void userFindByEmailTest() throws Exception{
        Mockito.when(userService.findByEmail(userDto.getEmail())).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/email").param("email", userDto.getEmail()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.password").value(IsNull.nullValue()));
    }

    @Test
    public void userAddUserTest() throws Exception{
        given(userService.add(any(User.class))).willReturn(user);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/user")
                .content(mapper.writeValueAsString(userDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId().intValue())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.password").value(IsNull.nullValue()));
    }

    @Test
    public void userFindByIdTest() throws Exception{
        Mockito.when(userService.get(user.getId())).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/user/id")
                .param("id", userDto.getId().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.password").value(IsNull.nullValue()));
    }


}
```

차근차근 살펴갑니다.

## 3.1 @WebMvcTest

**@WebMvcTest 어노테이션을 사용함으로써 얻을 수 있는 이점은 다음과 같습니다.**

1. HTTP server 를 실행하지 않고도, 컨트롤러 테스트가 가능하다.
2. 웹상에서 요청과 응답에 대한 테스트를 할 수 있다.
3. 모든 설정 정보가 로드되는 것이 아니기 때문에, 테스트가 조금 가볍다. 여전히 무겁긴 합니다.
4.  Spring security, @AutoConfigureWebMvc, @AutoConfigureMockMvc, @Controller, @ControllerAdvice 같은 설정을 포함하기 때문에, 컨트롤러 레이어의 테스트를 용이하게 한다.



**주의점**

4번에서 보다시피 서비스 레이어는 배제되기 때문에, 서비스 레이어에 대한 목 객체를 만드시 만들어주셔야 합니다.



## 3.2 @ActiveProfiles

이전에도 설명했듯이, dev 라는 설정 파일을 로드합니다.



## 3.3 코드

```java
@Test
public void userFindByIdTest() throws Exception{
    Mockito.when(userService.get(user.getId())).thenReturn(user);

    mockMvc.perform(MockMvcRequestBuilders
            .get("/user/id")
            .param("id", userDto.getId().toString())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(user.getId().intValue())))
            .andExpect(jsonPath("$.email", is(user.getEmail())))
            .andExpect(jsonPath("$.password").value(IsNull.nullValue()));
}

@Test
    public void userAddUserTest() throws Exception{
        given(userService.add(any(User.class))).willReturn(user);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/user")
                .content(mapper.writeValueAsString(userDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId().intValue())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.password").value(IsNull.nullValue()));
    }
```

get, post 코드를 보면서 익혀보겠습니다.

stubbing method를 설정하는 방법은 제가 공부하면서 크게 2가지로 확인하였습니다.



### given , when

위에서 보다시피, 특정 메소드가 호출될때, 목 객체 메소드의 특정한 값을 반환하도록  지정할 수 있습니다.

### password

mockMvc 같은 경우 널 값을 반환할때 is(null) 로 체크가 안됩니다. 따라서 특정 json 값이 null인지 

jsonPath("$.<name>").value(IsNull.nullValue()) 요렇게 체크하셔야 됩니다.



## 3.4 대망의 삽질

mockMvc 를 통해 post 요청을 테스트하고자 했을때, 계속해서 null 을 반환하여, 제가 지정한 user 라는 데이터를 반환하지 못했습니다.

stackOverflow 를 참고해서 확인해보니, 반환하고자 하는 값에 equals 를 적절하게 구현하지 않으면, 기존의 equals 로 동일성을 비교하는데

이 경우에 컨트롤러가 받는 실제 User 인스턴스가 직렬화 되었기때문에 User 클래스에서 equals() or hashcode() 가 재정의한 경우에만 동작합니다.



따라서 여기서는 단순히 any(object) 를 사용하여, 어떠한 값이 들어오더라도, 항상 동일한 반환값을 지정할 수 있게 했습니다.