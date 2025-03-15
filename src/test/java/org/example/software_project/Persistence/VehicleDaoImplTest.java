package org.example.software_project.Persistence;

import org.example.software_project.business.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VehicleDaoImplTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @InjectMocks
    private VehicleDaoImpl vehicleDao;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(mockConnection.prepareStatement(any(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong(1)).thenReturn(1L);
    }

    @Test
    void testGetAllVehicles() throws Exception {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getLong("id")).thenReturn(1L);
        when(mockResultSet.getLong("seller_id")).thenReturn(10L);
        when(mockResultSet.getString("make")).thenReturn("Ford");
        when(mockResultSet.getString("model")).thenReturn("Fiesta");
        when(mockResultSet.getInt("year")).thenReturn(2009);
        when(mockResultSet.getDouble("price")).thenReturn(3500.0);
        when(mockResultSet.getInt("mileage")).thenReturn(190000);
        when(mockResultSet.getString("fuel_type")).thenReturn("Petrol");
        when(mockResultSet.getString("transmission")).thenReturn("Manual");
        when(mockResultSet.getString("category")).thenReturn("Hatchback");
        when(mockResultSet.getString("description")).thenReturn("Car in great condition.");
        when(mockResultSet.getString("location")).thenReturn("Louth");
        when(mockResultSet.getString("status")).thenReturn("Available");
        when(mockResultSet.getString("image_url")).thenReturn("/images/ford_fiesta.jpg");

        List<Vehicle> vehicles = vehicleDao.getAllVehicles();

        assertNotNull(vehicles);
        assertEquals(1, vehicles.size());
        assertEquals(1L, vehicles.get(0).getId());
        assertEquals("Ford", vehicles.get(0).getMake());
        assertEquals("Fiesta", vehicles.get(0).getModel());
        assertEquals(2009, vehicles.get(0).getYear());
        assertEquals(3500.0, vehicles.get(0).getPrice());
        assertEquals(190000, vehicles.get(0).getMileage());
        assertEquals("Petrol", vehicles.get(0).getFuelType());
        assertEquals("Manual", vehicles.get(0).getTransmission());
        assertEquals("Hatchback", vehicles.get(0).getCategory());
        assertEquals("Car in great condition.", vehicles.get(0).getDescription());
        assertEquals("Louth", vehicles.get(0).getLocation());
        assertEquals("Available", vehicles.get(0).getStatus());
        assertEquals("/images/ford_fiesta.jpg", vehicles.get(0).getImage_url());
    }
    @Test
    void testSaveVehicle() throws Exception {

        Vehicle vehicle = new Vehicle();
        vehicle.setSellerId(100L);
        vehicle.setMake("Toyota");
        vehicle.setModel("Corolla");
        vehicle.setYear(2024);
        vehicle.setPrice(30000);
        vehicle.setMileage(25000);
        vehicle.setFuelType("Petrol");
        vehicle.setTransmission("Automatic");
        vehicle.setCategory("Sedan");
        vehicle.setDescription("A good car car.");
        vehicle.setLocation("Dublin");
        vehicle.setStatus("Available");

        String fileName = "tesla.png";

        vehicleDao.saveVehicle(vehicle, fileName);

        verify(mockPreparedStatement, times(1)).executeUpdate();

        assertEquals(1L, vehicle.getId());
        assertEquals("/images/tesla.png", vehicle.getImage_url());
    }
}
