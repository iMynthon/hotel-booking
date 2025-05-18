package com.example.hotelbookingapplication;

import com.example.hotelbookingapplication.dto.request.HotelEstimateRequest;
import com.example.hotelbookingapplication.dto.request.UpsertHotelRequest;
import com.example.hotelbookingapplication.model.Hotel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


public class HotelControllerTest extends AbstractTest{

    @Test
    @DisplayName("Тестовый поиск всех отелей и проверка работы валидатора параметров запроса")
    @WithMockUser(username = "user",roles = "USER")
    public void testFindAll() throws Exception{

        mockMvc.perform(get("/api/hotel?pageNumber=0&pageSize=10&city=Москва"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hotels.length()").value(2));

        mockMvc.perform(get("/api/hotel?pageNumber=0&pageSize=10&cito=Москва"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("тестовый поиск отеля по id")
    @WithMockUser(username = "user",roles = "USER")
    public void testFindById() throws Exception{
        Hotel expectedHotel = hotelRepository.findByNameIgnoreCase("Grand Plaza").orElseThrow();
        assertNotNull(expectedHotel);
        mockMvc.perform(get("/api/hotel/{id}",expectedHotel.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(expectedHotel.getName()))
                .andExpect(jsonPath("$.id").value(expectedHotel.getId()))
                .andExpect(jsonPath("$.rooms.length()").value(2));
    }

    @Test
    @DisplayName("Тестовое сохранение сущности Hotel")
    @WithMockUser(username = "admin",roles = "ADMIN")
    public void testSaveHotel() throws Exception{
        UpsertHotelRequest request = UpsertHotelRequest.builder()
                .name("save hotel")
                .title("save title")
                .address("save address")
                .city("save city")
                .distanceFromCenterCity((float) 0.5)
                .build();

        assertEquals(2,hotelRepository.count());

        mockMvc.perform(post("/api/hotel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.id").isNotEmpty());

        assertEquals(3,hotelRepository.count());
    }

    @Test
    @DisplayName("Тестовое оценивание отеля")
    @WithMockUser(username = "user",roles = "USER")
    public void testEstimateHotel() throws Exception{

        HotelEstimateRequest request = HotelEstimateRequest
                .builder()
                .name("Grand Plaza")
                .newMark(new BigDecimal("3.2"))
                .build();

        mockMvc.perform(post("/api/hotel/estimate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name").value(request.name()))
                .andExpect(jsonPath("$.numberOfRating").value(121))
                .andExpect(jsonPath("$.rating").value("3.99"));


    }

    @Test
    @DisplayName("Тестовое обновление сущности Hotel")
    @WithMockUser(username = "admin",roles = "ADMIN")
    public void testUpdateHotel() throws Exception{
        UpsertHotelRequest request = UpsertHotelRequest.builder()
                .name("update hotel")
                .title("update title")
                .address("update address")
                .city("update city")
                .distanceFromCenterCity((float) 0.8)
                .build();

        Hotel deprecatedHotel = hotelRepository.findByNameIgnoreCase("Grand Plaza").orElseThrow();

        assertEquals(2,hotelRepository.count());

        mockMvc.perform(put("/api/hotel/{id}",deprecatedHotel.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.id").value(deprecatedHotel.getId()));

        assertEquals(2,hotelRepository.count());
    }

    @Test
    @DisplayName("Тестовое удаление сущности Hotel")
    @WithMockUser(username = "admin",roles = "ADMIN")
    public void testDeleteById() throws Exception{
        Hotel hotel = hotelRepository.findByNameIgnoreCase("Grand Plaza").orElseThrow();

        assertEquals(2,hotelRepository.count());

        mockMvc.perform(delete("/api/hotel/{id}",hotel.getId()))
                .andExpect(status().isNoContent());

        assertEquals(1,hotelRepository.count());
    }
}
