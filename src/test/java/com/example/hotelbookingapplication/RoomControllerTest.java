package com.example.hotelbookingapplication;

import com.example.hotelbookingapplication.dto.request.UpsertRoomRequest;
import com.example.hotelbookingapplication.model.jpa.Room;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class RoomControllerTest extends AbstractTest{

    @Test
    @DisplayName("Тестовый поиск всех комнат и проверка валидатора параметров")
    @WithMockUser(username = "user",roles = "USER")
    public void testFindAll() throws Exception {

        mockMvc.perform(get("/api/room"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rooms.length()").value(4));

        mockMvc.perform(get("/api/room?pageNumber=0&pageSize=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rooms.length()").value(2));

        mockMvc.perform(get("/api/room?pageNumber=0&pageSize=2&nama=Available Room"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Тестовый поиск Room по id")
    @WithMockUser(username = "user",roles = "USER")
    public void testFindById() throws Exception{
        Room luxuryRoom = roomRepository.findByName("Luxury Room").orElseThrow();
        mockMvc.perform(get("/api/room/{id}",luxuryRoom.getId()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name").value(luxuryRoom.getName()))
                .andExpect(jsonPath("$.unavailableDate.length()").value(2))
                .andExpect(jsonPath("$.hotel").value("Grand Plaza"));
    }

    @Test
    @DisplayName("Тестовое сохранение Room с другой роли аутентификации")
    @WithMockUser(username = "user",roles = "USER")
    public void testSaveAndUpdateRoomAnyRole() throws Exception {
        UpsertRoomRequest request = UpsertRoomRequest.builder()
                .name("Persist room")
                .description("Test persist room")
                .number(567)
                .price(500)
                .unavailableDate(List.of("15.02.2020", "10.08.2017"))
                .numberOfPeople(5)
                .hotel("Grand Plaza")
                .build();

        assertEquals(4, roomRepository.count());

        mockMvc.perform(post("/api/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        Room derpecatedRoom = roomRepository.findByName("Standard Room").orElseThrow();

        mockMvc.perform(put("/api/room/{id}",derpecatedRoom.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());


    }

    @Test
    @DisplayName("Тестовое сохранение м обновление Room")
    @WithMockUser(username = "admin",roles = "ADMIN")
    public void testSaveAndUpdateRoom() throws Exception{
        UpsertRoomRequest request = UpsertRoomRequest.builder()
                .name("Persist room")
                .description("Test persist room")
                .number(567)
                .price(500)
                .unavailableDate(List.of("15.02.2020","10.08.2017"))
                .numberOfPeople(5)
                .hotel("Grand Plaza")
                .build();

        assertEquals(4,roomRepository.count());

        mockMvc.perform(post("/api/room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.unavailableDate.length()").value(2))
                .andExpect(jsonPath("$.hotel").value("Grand Plaza"));

        assertEquals(5,roomRepository.count());

        UpsertRoomRequest requestUpdate = UpsertRoomRequest.builder()
                .name("merge room")
                .description("Test merge room")
                .number(567)
                .price(600)
                .unavailableDate(new ArrayList<>(List.of("13.05.2025", "12.07.2024")))
                .numberOfPeople(5)
                .hotel("Grand Plaza")
                .build();

        Room derpecatedRoom = roomRepository.findByName(request.getName()).orElseThrow();

        mockMvc.perform(put("/api/room/{id}",derpecatedRoom.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(derpecatedRoom.getId()))
                .andExpect(jsonPath("$.name").value(requestUpdate.getName()))
                .andExpect(jsonPath("$.description").value(requestUpdate.getDescription()))
                .andExpect(jsonPath("$.hotel").value(requestUpdate.getHotel()));

        assertEquals(5,roomRepository.count());
    }


    @Test
    @DisplayName("Тестовое удаление Room")
    @WithMockUser(username = "admin",roles = "ADMIN")
    public void testDeleteByIdRoom() throws Exception{
        Room room = roomRepository.findByName("Available Room").orElseThrow();

        assertEquals(4,roomRepository.count());

        mockMvc.perform(delete("/api/room/{id}",room.getId()))
                .andExpect(status().isNoContent());

        assertEquals(3,roomRepository.count());
    }
}
