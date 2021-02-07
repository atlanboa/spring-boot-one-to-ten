package com.plee.auth.service;

import com.plee.auth.domain.User;
import com.plee.auth.exception.UserExistedException;
import com.plee.auth.exception.UserNotFoundException;
import com.plee.auth.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void addUserSuccessfully() {
        final User user = new User(null, "name", "password");
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.empty());
        given(userRepository.save(user)).willReturn(user);

        User savedUser = userService.add(user);
        Assertions.assertNotNull(savedUser);
        verify(userRepository).findByEmail(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void addUserFailure() {
        final User user = new User(1L, "name", "password");
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        Assertions.assertThrows(UserExistedException.class, () -> userService.add(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserSuccessfully() {
        final User user = new User(1L, "name", "password");
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        User userByGet = userService.get(user.getId());
        Assertions.assertEquals(user, userByGet);
        verify(userRepository).findById(anyLong());
    }

    @Test
    void getUserFailure() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.get(anyLong()));
        verify(userRepository).findById(anyLong());
    }

    @Test
    void updateSuccessfully() {
        final User user = new User(1L, "name", "password");
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(userRepository.save(user)).willReturn(user);
        User updatedUser = userService.update(user);
        Assertions.assertNotNull(updatedUser);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateFailure() {
        final User user = new User(1L, "name", "password");
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.update(user));
        verify(userRepository).findById(anyLong());
    }

    @Test
    void deleteSuccessfully() {
        final Long id = 1L;
        userService.delete(id);
        verify(userRepository, times(1)).deleteById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());

    }

    @Test
    void findByEmailSuccessfully() {
        final User user = new User(1L, "name", "password");
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        User userByEmail = userService.findByEmail(user.getEmail());
        Assertions.assertEquals(user, userByEmail);
        verify(userRepository).findByEmail(any(String.class));
    }

    @Test
    void findByEmailFailure() {
        final String email = "email";
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.findByEmail(email));
        verify(userRepository).findByEmail(any(String.class));
    }
}