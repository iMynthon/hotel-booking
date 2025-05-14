package com.example.hotelbookingapplication.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity(name = "hotels")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String title;

    private String city;

    private String address;
    @Column(name = "distance_from_center_city")
    private Double distanceFromCenterCity;

    private Float rating;
    @Column(name = "number_of_rating")
    private Integer numberOfRating;

    @Builder.Default
    @OneToMany(mappedBy = "hotel",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Room> rooms = new ArrayList<>();

}
