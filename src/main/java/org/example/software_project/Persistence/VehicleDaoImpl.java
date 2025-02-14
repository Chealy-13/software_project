package org.example.software_project.Persistence;

import org.example.software_project.business.Vehicle;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of VehicleDao for interacting with the Vehicles table in MySQL.
 */
@Repository
public class VehicleDaoImpl extends MySQLDao implements VehicleDao {

    /**
     * Gets a list of all vehicles stored in the database.
     * It establishes a connection, executes an SQL query to fetch all vehicle records,
     * and maps the results to Vehicle objects.
     *
     * @return A list of Vehicle objects representing all vehicles in the database.
     * Returns an empty list if no vehicles are found.
     */
    @Override
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        Connection conn = super.getConnection();

        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Vehicles")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(mapRowToVehicle(rs));
                }
            } catch (SQLException e) {
                System.out.println("SQL Exception occurred when executing SQL or processing results.");
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception occurred when attempting to prepare SQL for execution");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            super.freeConnection(conn);
        }

        return vehicles;
    }

    /**
     * Saves a new vehicle listing to the database.
     *
     * @param vehicle The vehicle object containing details to be stored.
     */
    @Override
    public void saveVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO Vehicles (seller_id, make, model, year, price, mileage, fuel_type, transmission, category, description, location, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = super.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, vehicle.getSellerId());
            ps.setString(2, vehicle.getMake());
            ps.setString(3, vehicle.getModel());
            ps.setInt(4, vehicle.getYear());
            ps.setDouble(5, vehicle.getPrice());
            ps.setInt(6, vehicle.getMileage());
            ps.setString(7, vehicle.getFuelType());
            ps.setString(8, vehicle.getTransmission());
            ps.setString(9, vehicle.getCategory());
            ps.setString(10, vehicle.getDescription());
            ps.setString(11, vehicle.getLocation());
            ps.setString(12, "Available");

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        vehicle.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            super.freeConnection(conn);
        }

    }

    /**
     * Maps a row from the ResultSet to a Vehicle object.
     *
     * @param rs the ResultSet containing the vehicle data.
     * @return A Vehicle object populated with data from the current row of the result set.
     * @throws SQLException If an SQL error occurs while retrieving values from the result set.
     */
        private Vehicle mapRowToVehicle (ResultSet rs) throws SQLException {
            return new Vehicle(
                    rs.getLong("id"),
                    rs.getLong("seller_id"),
                    rs.getString("make"),
                    rs.getString("model"),
                    rs.getInt("year"),
                    rs.getDouble("price"),
                    rs.getInt("mileage"),
                    rs.getString("fuel_type"),
                    rs.getString("transmission"),
                    rs.getString("category"),
                    rs.getString("description"),
                    rs.getString("location"),
                    rs.getString("status")
            );
        }
    }
