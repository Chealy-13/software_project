package org.example.software_project.Persistence;


import org.example.software_project.business.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

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
        when(mockConnection.prepareStatement(any())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }


    @Test
    void testGetAllVehicles() throws Exception {
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("make")).thenReturn("Ford");
        when(mockResultSet.getString("model")).thenReturn("Fiesta");
        when(mockResultSet.getInt("year")).thenReturn(2009);
        when(mockResultSet.getDouble("price")).thenReturn(3500.0);
        when(mockResultSet.getInt("mileage")).thenReturn(190000);
        when(mockResultSet.getString("fuel_type")).thenReturn("Petrol");
        when(mockResultSet.getString("transmission")).thenReturn("Manual");
        when(mockResultSet.getString("category")).thenReturn("Hatchback");
        when(mockResultSet.getString("description")).thenReturn("car");
        when(mockResultSet.getString("location")).thenReturn("louth");
        when(mockResultSet.getString("status")).thenReturn("now");

        List<Vehicle> vehicles = vehicleDao.getAllVehicles();

        assertNotNull(vehicles);
        assertEquals(1, vehicles.size());
        assertEquals(1, vehicles.getFirst().getId());
        assertEquals("Ford", vehicles.getFirst().getMake());
        assertEquals("Fiesta", vehicles.getFirst().getModel());
        assertEquals(2009, vehicles.getFirst().getYear());
        assertEquals(3500, vehicles.getFirst().getPrice());
        assertEquals(190000, vehicles.getFirst().getMileage());
        assertEquals("Petrol", vehicles.getFirst().getFuelType());
        assertEquals("Manual", vehicles.getFirst().getTransmission());
        assertEquals("Hatchback", vehicles.getFirst().getCategory());
        assertEquals("car", vehicles.getFirst().getDescription());
        assertEquals("louth", vehicles.getFirst().getLocation());
        assertEquals("now", vehicles.getFirst().getStatus());
    }
}
