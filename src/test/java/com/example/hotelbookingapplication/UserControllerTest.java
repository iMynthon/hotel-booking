package com.example.hotelbookingapplication;
import com.example.hotelbookingapplication.dto.request.UpsertUserRequest;
import com.example.hotelbookingapplication.model.jpa.Authority;
import com.example.hotelbookingapplication.model.jpa.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import java.util.concurrent.TimeUnit;

import static com.example.hotelbookingapplication.model.jpa.RoleType.ROLE_ADMIN;
import static com.example.hotelbookingapplication.model.jpa.RoleType.ROLE_USER;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@EnableAspectJAutoProxy
public class UserControllerTest extends AbstractTest{

    @Test
    @DisplayName("Тестовый поиск User по id и имени пользователя")
    @WithMockUser(username = "Администратор системы",roles = "ADMIN")
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
    @DisplayName("Тестовый поиск User по id и имени пользователя на проверку прав просмотра и через AOP")
    @WithMockUser(username = "Пользователь системы",roles = "USER")
    public void testFindByIdAndUsernameAnyUser() throws Exception{

        User admin = userRepository.findByUsernameIgnoreCase("Администратор системы").orElseThrow();

        mockMvc.perform(get("/api/user/id/{id}",admin.getId()))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/user/username/{username}",admin.getUsername()))
                .andExpect(status().isForbidden());

        User user2 = userRepository.findByUsernameIgnoreCase("Пользователь системы 2").orElseThrow();

        mockMvc.perform(get("/api/user/id/{id}",user2.getId()))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/user/username/{username}",user2.getUsername()))
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("Тестовая проверка на дупликат имени пользователя при сохранении")
    public void testSaveDuplicate() throws Exception{
        UpsertUserRequest request = UpsertUserRequest.builder()
                .username("Администратор системы")
                .email("nikola@mail.ru")
                .password("12457890")
                .build();

        assertEquals(3,userRepository.count());

        mockMvc.perform(post("/api/user?role=ROLE_USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        assertEquals(3,userRepository.count());
    }

    @Test
    @DisplayName("Тестовое сохранение User и его сохранение его роли и сохранение статистики в MongoDB с помощью Kafka")
    public void testSaveUserAndRoleToAuthority() throws Exception{
        UpsertUserRequest request = UpsertUserRequest.builder()
                .username("Nikola")
                .email("nikola@mail.ru")
                .password("12457890")
                .build();

        assertEquals(3,userRepository.count());
        assertEquals(3,registrationUserStatsRepository.count());

        MvcResult result = mockMvc.perform(post("/api/user?role=ROLE_USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").value(request.getUsername()))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        Integer userId = objectMapper.readTree(responseContent).get("id").asInt();

        await().atMost(15, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new AssertionError("User not found"));
                    Authority authority = authorityRepository.findByUserId(user.getId())
                            .orElseThrow(() -> new AssertionError("Authority not found"));
                    assertEquals(ROLE_USER,authority.getRole());
                });

        mockMvc.perform(post("/api/user?role=ROLE_USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User с таким именем - {Nikola} уже зарегистрирован"));


        assertEquals(4,userRepository.count());
        assertEquals(4,registrationUserStatsRepository.count());
    }

    @Test
    @DisplayName("Тестовое обновление User")
    @WithMockUser(username = "Пользователь системы",roles = "USER")
    public void testUpdateUser() throws Exception{
        UpsertUserRequest request = UpsertUserRequest.builder()
                .username("update")
                .email("update@mail.ru")
                .password("1256")
                .build();

        User user = userRepository.findByUsernameIgnoreCase("Пользователь системы").orElseThrow();

        assertTrue(user.getRoles().stream().anyMatch(a -> a.getRole().equals(ROLE_USER)));
        assertEquals(3,userRepository.count());

        mockMvc.perform(put("/api/user/{id}",user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").value(request.getUsername()));


        assertEquals(3,userRepository.count());

    }

    @Test
    @DisplayName("Тестовое обновление User со сменов роли")
    @WithMockUser(username = "Пользователь системы", roles = "USER")
    public void testUpdateUserChangeRole() throws Exception{
        UpsertUserRequest newRequest = UpsertUserRequest.builder()
                .username("new update")
                .email("newupdate@mail.ru")
                .password("1280")
                .build();
        User user = userRepository.findByUsernameIgnoreCase("Пользователь системы").orElseThrow();

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
    @WithMockUser(username = "Администратор системы",roles = "ADMIN")
    public void testDeleteById() throws Exception{

        User user2 = userRepository.findByUsernameIgnoreCase("Пользователь системы").orElseThrow();

        assertEquals(3,userRepository.count());
        assertEquals(3,authorityRepository.count());


        mockMvc.perform(delete("/api/user/{id}", user2.getId()))
                        .andExpect(status().isNoContent());

        assertEquals(2,authorityRepository.count());
        assertEquals(2,userRepository.count());
    }
}
