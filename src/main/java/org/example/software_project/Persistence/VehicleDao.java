package org.example.software_project.Persistence;

import org.example.software_project.business.Vehicle;

import java.util.List;

public interface VehicleDao {
    List<Vehicle> getAllVehicles();
    void saveVehicle(Vehicle vehicle); // Add this method to save a vehicle

}
