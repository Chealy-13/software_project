package org.example.software_project.controllers;

import jakarta.servlet.http.HttpSession;
import org.example.software_project.Persistence.VehicleDao;
import org.example.software_project.business.User;
import org.example.software_project.business.Vehicle;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller class for handling vehicle-related operations.
 */
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
     * @return the name of the Thymeleaf template ("allVehicles")
     */
    @GetMapping("/vehicles")
    public String getAllVehicles(Model model) {
        List<Vehicle> vehicles = vehicleDao.getAllVehicles();
        model.addAttribute("vehicles", vehicles);
        return "allVehicles";
    }

    @GetMapping("/vehicles/add")
    public String showAddCarForm() {
        return "addCar";
    }

    @PostMapping("/vehicles/add")
    public String addCar(@RequestParam String make,
                         @RequestParam String model,
                         @RequestParam int year,
                         @RequestParam double price,
                         @RequestParam int mileage,
                         @RequestParam String fuelType,
                         @RequestParam String transmission,
                         @RequestParam String category,
                         @RequestParam String description,
                         @RequestParam String location,
                         HttpSession session, RedirectAttributes redirectAttributes) {
        User seller = (User) session.getAttribute("currentUser");

        if (seller == null) {
            return "redirect:/loginPage";
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setSellerId((long) seller.getId());
        vehicle.setMake(make);
        vehicle.setModel(model);
        vehicle.setYear(year);
        vehicle.setPrice(price);
        vehicle.setMileage(mileage);
        vehicle.setFuelType(fuelType);
        vehicle.setTransmission(transmission);
        vehicle.setCategory(category);
        vehicle.setDescription(description);
        vehicle.setLocation(location);
        vehicle.setStatus("Available");

        vehicleDao.saveVehicle(vehicle);

        redirectAttributes.addFlashAttribute("successMessage", "Car successfully added!");

        return "redirect:/";
    }


}