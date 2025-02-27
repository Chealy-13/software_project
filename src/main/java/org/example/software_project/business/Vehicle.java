package org.example.software_project.business;

import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    private Long id;
    private Long sellerId;
    private String make;
    private String model;
    private int year;
    private double price;
    private int mileage;
    private String fuelType;
    private String transmission;
    private String category;
    private String description;
    private String location;
    private String status;
    private String image_url;


    }

