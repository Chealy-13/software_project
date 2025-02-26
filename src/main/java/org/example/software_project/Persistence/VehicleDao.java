package org.example.software_project.Persistence;

import org.example.software_project.business.Vehicle;

import java.util.List;

public interface VehicleDao {

    void saveVehicle(Vehicle vehicle);

    List<Vehicle> getAllVehicles();

    List<Vehicle> searchVehicles(String keyword, Integer minPrice, Integer maxPrice, Integer minYear, Integer maxYear, Integer mileage, String fuelType, String location, String sortBy);

    List<Vehicle> getAllVehiclesWithImages();
}
