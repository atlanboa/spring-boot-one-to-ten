package com.plee.auth.service;

import com.plee.auth.domain.User;
import com.plee.auth.exception.UserExistedException;
import com.plee.auth.exception.UserNotFoundException;
import com.plee.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    final boolean DELETE_SUCCESS = true;
    final boolean DELETE_FAILED = false;

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User add(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent())
            throw new UserExistedException("User is already joined : "+user.getEmail());
        else
            return userRepository.save(user);
    }

    @Override
    public User get(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("delete: User not found by : " + id));
    }

    @Override
    public User update(User user) {
        if (userRepository.findById(user.getId()).isPresent())
            return userRepository.save(user);
        else
            throw new UserNotFoundException("update: User not found by : " + user.getId());
    }

    @Override
    public boolean delete(Long id) {
        userRepository.deleteById(id);
        if (!userRepository.findById(id).isPresent())
            return DELETE_SUCCESS;
        else
            return DELETE_FAILED;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("findbyEmail: User not found by : " + email));
    }
}
