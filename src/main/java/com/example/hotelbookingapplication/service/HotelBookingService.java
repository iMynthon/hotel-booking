package com.example.hotelbookingapplication.service;

public interface HotelBookingService<T>{

    T findById(Integer id);

    T save(T entity);

    T update(T entity);

    void deleteById(Integer id);
}
