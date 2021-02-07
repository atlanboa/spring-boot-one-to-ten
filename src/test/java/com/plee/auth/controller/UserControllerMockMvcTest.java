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