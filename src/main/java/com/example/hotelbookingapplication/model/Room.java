package com.example.hotelbookingapplication.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
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

    @Column(name = "number_of_people")
    private Integer numberOfPeople;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "unavailable_date")
    private List<LocalDate> unavailableDate = new ArrayList<>();

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE},fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @Builder.Default
    @OneToMany(mappedBy = "room",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();
}
