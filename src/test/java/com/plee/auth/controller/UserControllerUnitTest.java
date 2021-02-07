package com.plee.auth.controller;

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
        final UserDto testUserDto = UserDto.builder()
                .id(testUser.getId())
                .email(testUser.getEmail())
                .password(testUser.getPassword())
                .build();

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
        final UserDto testUserDto = UserDto.builder()
                .id(testUser.getId())
                .email(testUser.getEmail())
                .password(testUser.getPassword())
                .build();

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