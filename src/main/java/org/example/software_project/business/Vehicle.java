package org.example.software_project.business;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {
    @EqualsAndHashCode.Include
    private int id;
    @NonNull
    private String make;
    @NonNull
    private String model;
    @NonNull
    private int year;
    @NonNull
    private double price;
    @NonNull
    private int mileage;
    @NonNull
    private String fuelType;
    @NonNull
    private String transmission;
    @NonNull
    private String category;
    private String description;
    @NonNull
    private String location;
    @NonNull
    private String status;

    @Override
    public String toString() {
        return "Vehicle ID: " + id +
                "\nMake: " + make +
                "\nModel: " + model +
                "\nYear: " + year +
                "\nPrice: $" + price +
                "\nMileage: " + mileage +
                "\nFuel Type: " + fuelType +
                "\nTransmission: " + transmission +
                "\nCategory: " + category +
                "\nLocation: " + location +
                "\nStatus: " + status;
    }
}