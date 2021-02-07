package com.plee.auth.service;

import com.plee.auth.domain.User;
import com.plee.auth.exception.UserExistedException;
import com.plee.auth.exception.UserNotFoundException;
import com.plee.auth.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class UserServiceImplIntegrationTest {
    @Autowired
    private UserService userService;

    @Test
    public void addSuccessTest() {
        final User user = new User(null, "email1", "password");
        User savedUser = userService.add(user);
        Assertions.assertEquals(user.getEmail(), savedUser.getEmail());
    }

    @Test
    public void addFailureTest() {
        final User user = new User(null, "email1", "password");
        userService.add(user);
        Assertions.assertThrows(UserExistedException.class, () ->userService.add(user));
    }

    @Test
    public void getSuccessTest() {
        final User user = new User(null, "email1", "password");
        User savedUser = userService.add(user);
        Assertions.assertEquals(savedUser, userService.get(savedUser.getId()));
    }

    @Test
    public void getFailureTest() {
        final User user = new User(null, "email1", "password");
        User savedUser = userService.add(user);
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.get(savedUser.getId() + 1));
    }

}
