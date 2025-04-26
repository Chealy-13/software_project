package org.example.software_project.Persistence;

import org.example.software_project.business.User;
import org.example.software_project.business.Vehicle;

import java.util.List;

public interface VehicleDao {


    void saveVehicle(Vehicle vehicle, String fileName);

    List<Vehicle> getAllVehicles();

    List<Vehicle> searchVehicles(String keyword, Integer minPrice, Integer maxPrice, Integer minYear, Integer maxYear, Integer mileage, String fuelType, String location, String sortBy);

    List<Vehicle> getAllVehiclesWithImages();

    List<Vehicle> getVehiclesBySeller(int sellerId);

    void deleteVehicle(Long listingId);

    void updateVehicle(Long id, String make, String model, double price);

    Vehicle getVehicleById(Long vehicleId);

    void setFlagStatus(Long listingId, boolean flagged);

    List<Vehicle> getFlaggedVehicles();
}
