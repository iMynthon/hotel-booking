package com.example.hotelbookingapplication;
import com.example.hotelbookingapplication.dto.request.UpsertUserRequest;
import com.example.hotelbookingapplication.model.Authority;
import com.example.hotelbookingapplication.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.hotelbookingapplication.model.RoleType.ROLE_ADMIN;
import static com.example.hotelbookingapplication.model.RoleType.ROLE_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@AutoConfigureMockMvc
public class UserControllerTest extends AbstractTest{

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Тестовый поиск User по id и имени пользователя")
    @WithMockUser(username = "admin",roles = "ADMIN")
    public void testFindByIdAndUsernameToUser() throws Exception{

        User admin = userRepository.findByUsernameIgnoreCase("Администратор системы").orElseThrow();

        mockMvc.perform(get("/api/user/id/{id}",admin.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(admin.getUsername()))
                .andExpect(jsonPath("$.roles.length()").value(1));

        mockMvc.perform(get("/api/user/username/{username}",admin.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(admin.getId()))
                .andExpect(jsonPath("$.roles.length()").value(1));

        assertTrue(admin.getRoles().stream().anyMatch(a -> a.getRole().equals(ROLE_ADMIN)));
    }

    @Test
    @DisplayName("Тестовое сохранение User и его сохранение его роли")
    public void testSaveUserAndRoleToAuthority() throws Exception{
        UpsertUserRequest request = UpsertUserRequest.builder()
                .username("Nikola")
                .email("nikola@mail.ru")
                .password("12457890")
                .build();

        assertEquals(2,userRepository.count());

        mockMvc.perform(post("/api/user?role=ROLE_USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").value(request.getUsername()));

        assertEquals(3,userRepository.count());

        User user = userRepository.findByUsernameIgnoreCase(request.getUsername()).orElseThrow();
        Authority authority = authorityRepository.findByUserId(user.getId()).orElseThrow();
        assertEquals(ROLE_USER,authority.getRole());

        mockMvc.perform(post("/api/user?role=ROLE_USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Тестовое обновление User")
    @WithMockUser(username = "user",roles = "USER")
    public void updateUser_WhenValidRequest_ShouldUpdateAndChangeRole()throws Exception{
        UpsertUserRequest request = UpsertUserRequest.builder()
                .username("update")
                .email("update@mail.ru")
                .password("1256")
                .build();

        User user = userRepository.findByUsernameIgnoreCase("Пользователь системы").orElseThrow();

        assertTrue(user.getRoles().stream().anyMatch(a -> a.getRole().equals(ROLE_USER)));

        assertEquals(2,userRepository.count());

        mockMvc.perform(put("/api/user/{id}",user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").value(request.getUsername()));

        assertEquals(2,userRepository.count());

        mockMvc.perform(put("/api/user/{id}",user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        UpsertUserRequest newRequest = UpsertUserRequest.builder()
                .username("new update")
                .email("newupdate@mail.ru")
                .password("1280")
                .build();

        mockMvc.perform(put("/api/user/{id}?role=ROLE_ADMIN",user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").value(newRequest.getUsername()));

        User newUpdate = userRepository.findByUsernameIgnoreCase(newRequest.getUsername()).orElseThrow();

        assertTrue(newUpdate.getRoles().stream().anyMatch(authority -> authority.getRole().equals(ROLE_ADMIN)));
    }

    @Test
    @DisplayName("Тестовое удаление User")
    @WithMockUser(username = "admin",roles = "ADMIN")
    public void testDeleteById() throws Exception{

        User admin = userRepository.findByUsernameIgnoreCase("Администратор системы").orElseThrow();

        assertEquals(2,userRepository.count());
        assertEquals(2,authorityRepository.count());

        mockMvc.perform(delete("/api/user/{id}",admin.getId()))
                .andExpect(status().isNoContent());

        assertEquals(1,userRepository.count());
        assertEquals(1,authorityRepository.count());
    }
}
