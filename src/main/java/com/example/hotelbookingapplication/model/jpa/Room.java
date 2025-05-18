package com.example.hotelbookingapplication.model.jpa;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
@Entity(name = "rooms")
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;

    private Integer number;

    private Integer price;

    @Column(name = "number_of_people")
    private Integer numberOfPeople;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "unavailable_date")
    @ToString.Exclude
    private List<LocalDate> unavailableDate = new ArrayList<>();

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(name = "hotel_id")
    @ToString.Exclude
    private Hotel hotel;

    @Builder.Default
    @OneToMany(mappedBy = "room",cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Booking> bookings = new ArrayList<>();
}
