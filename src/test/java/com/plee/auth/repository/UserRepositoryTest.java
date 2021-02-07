package com.plee.auth.repository;

import com.plee.auth.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles(value = "dev")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void saveUserTest() {
        User user = new User(null,"name", "password");
        User savedUser = userRepository.save(user);
        Assertions.assertEquals(user.getEmail(), savedUser.getEmail(),
                "saveUserTest");
        Assertions.assertEquals(user.getPassword(), savedUser.getPassword(),
                "saveUserTest");
    }

    @Test
    public void findByEmailSuccessTest() {
        User user = new User(null, "name1", "password");
        User savedUser = userRepository.save(user);

        Optional<User> userFindByEmail = userRepository.findByEmail(user.getEmail());
        userFindByEmail.ifPresent(value -> Assertions.assertEquals(savedUser.getEmail(), value.getEmail()));
    }

    @Test
    public void findByEmailFailureTest() {

        Optional<User> userFindByEmail = userRepository.findByEmail("not exist email");
        Assertions.assertEquals(Optional.empty(), userFindByEmail);
    }

    @Test
    public void idStrategyTest() {
        User user1 = new User(null, "name1", "password");
        User user2 = new User(null,"name2", "password");
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        Assertions.assertEquals(1, Math.abs(savedUser1.getId() - savedUser2.getId()));

    }

}