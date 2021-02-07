package com.plee.auth.service;

import com.plee.auth.domain.User;

public interface UserService {
    User add(User user);
    User get(Long id);
    User update(User user);
    boolean delete(Long id);
    User findByEmail(String email);
}
