package com.plee.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plee.auth.dto.UserDto;
import org.junit.jupiter.api.Test;

public class SampleTest {
    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test() throws JsonProcessingException {
        final UserDto userDto = UserDto.builder()
                .id(0L)
                .email("email")
                .password("password")
                .build();

        String result = mapper.writeValueAsString(userDto);
        System.out.println(result);
    }
}
