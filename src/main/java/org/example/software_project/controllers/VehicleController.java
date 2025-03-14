package org.example.software_project.controllers;
import jakarta.servlet.http.HttpSession;
import org.example.software_project.Persistence.VehicleDao;
import org.example.software_project.business.User;
import org.example.software_project.business.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;

@Controller
public class VehicleController {
    private final VehicleDao vehicleDao;

    private static final String UPLOAD_DIR = "src/main/resources/static/images/";

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
                         @RequestParam("imageFile") MultipartFile imageFile,
                         HttpSession session, RedirectAttributes redirectAttributes) {

        User seller = (User) session.getAttribute("currentUser");
        if (seller == null) {
            return "redirect:/loginPage";
        }

        String fileName = imageFile.getOriginalFilename();
        String uploadDir = "src/main/resources/static/images/";
        Path uploadPath = Paths.get(uploadDir);
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/vehicles?error=imageUploadFailed";
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setSellerId(Long.valueOf(seller.getId()));
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
        vehicle.setImage_url(Collections.singletonList("/images/" + fileName)); // Store relative image path

        vehicleDao.saveVehicle(vehicle, fileName);

        redirectAttributes.addFlashAttribute("successMessage", "Car successfully added!");
        return "redirect:/vehicles";
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
