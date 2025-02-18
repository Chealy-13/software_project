package org.example.software_project.controllers;

import org.example.software_project.Persistence.VehicleDao;
import org.example.software_project.business.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class VehicleController {
    private final VehicleDao vehicleDao;

    /**
     * Constructs a VehicleController with the specified VehicleDao.
     *
     * @param vehicleDao instance used for vehicle-related database operations
     */
    public VehicleController(VehicleDao vehicleDao) {
        this.vehicleDao = vehicleDao;
    }

    /**
     * Handles requests to the "/vehicles" endpoint.
     * Retrieves all vehicles from the database and adds them to the model for
     * rendering in the "vehicles" view.
     *
     * @param model used to pass data to the view
     * @return the name of the Thymeleaf template ("vehicles")
     */
    @GetMapping("/vehicles")
    public String getAllVehicles(Model model) {
        List<Vehicle> vehicles = vehicleDao.getAllVehicles();
        model.addAttribute("vehicles", vehicles);
        return "allVehicles";
    }

    @GetMapping("/searchVehicles")
    public String searchVehicles(@RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) Integer minPrice,
                                 @RequestParam(required = false) Integer maxPrice,
                                 @RequestParam(required = false) Integer minYear,
                                 @RequestParam(required = false) Integer maxYear,
                                 @RequestParam(required = false) Integer mileage,
                                 @RequestParam(required = false) String fuelType,
                                 @RequestParam(required = false) String location,
                                 @RequestParam(required = false) String sortBy,
                                 Model model) {

        List<Vehicle> vehicles = vehicleDao.searchVehicles(keyword, minPrice, maxPrice, minYear, maxYear, mileage, fuelType, location, sortBy);

        model.addAttribute("vehicles", vehicles);
        return "allVehicles";
    }

}
