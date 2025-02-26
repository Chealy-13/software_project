package org.example.software_project.Persistence;

import org.example.software_project.business.Vehicle;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class VehicleDaoImpl extends MySQLDao implements VehicleDao {
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
     * Gets a list of all vehicles stored in the database.
     * It establishes a connection, executes an SQL query to fetch all vehicle records,
     * and maps the results to Vehicle objects.
     *
     * @return A list of Vehicle objects representing all vehicles in the database.
     * Returns an empty list if no vehicles are found.
     */
    @Override
    public List<Vehicle> getAllVehicles() {
        // Create variable to hold the customer info from the database
        List<Vehicle> vehicles = new ArrayList<>();
        // Get a connection using the superclass
        Connection conn = super.getConnection();
        // Get a statement from the connection
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Vehicles")) {
            // Execute the query
            try (ResultSet rs = ps.executeQuery()) {
                // Repeatedly try to get a customer from the resultset
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
            // Close the connection using the superclass method
            super.freeConnection(conn);
        }

        return vehicles;
    }

    /**
     * Saves a new vehicle listing to the database.
     *
     * @param vehicle The vehicle object containing details to be stored.
     */


    /**
     * Maps a row from the ResultSet to a Vehicle object.
     *
     * @param rs the ResultSet containing the vehicle data.
     * @return A Vehicle object populated with data from the current row of the result set.
     * @throws SQLException If an SQL error occurs while retrieving values from the result set.
     */
    private Vehicle mapRowToVehicle(ResultSet rs) throws SQLException {
        List<String> image_url = new ArrayList<>(getVehicleImages(rs.getLong("id")));

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
                rs.getString("status"),
                image_url

        );
    }

    @Override
    public List<Vehicle> searchVehicles(String keyword, Integer minPrice, Integer maxPrice, Integer minYear, Integer maxYear, Integer mileage, String fuelType, String location, String sortBy) {
        List<Vehicle> vehicles = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Vehicles WHERE 1=1");

        if (keyword != null && !keyword.isEmpty()) {
            queryBuilder.append(" AND (make LIKE ? OR model LIKE ?)");
        }
        if (minPrice != null) {
            queryBuilder.append(" AND price >= ?");
        }
        if (maxPrice != null) {
            queryBuilder.append(" AND price <= ?");
        }
        if (minYear != null) {
            queryBuilder.append(" AND year >= ?");
        }
        if (maxYear != null) {
            queryBuilder.append(" AND year <= ?");
        }
        if (mileage != null) {
            queryBuilder.append(" AND mileage <= ?");
        }
        if (fuelType != null && !fuelType.isEmpty()) {
            queryBuilder.append(" AND fuel_type = ?");
        }
        if (location != null && !location.isEmpty()) {
            queryBuilder.append(" AND location LIKE ?");
        }

        if (sortBy != null) {
            if (sortBy.equals("price")) {
                queryBuilder.append(" ORDER BY price");
            } else if (sortBy.equals("year")) {
                queryBuilder.append(" ORDER BY year");
            } else if (sortBy.equals("newest")) {
                queryBuilder.append(" ORDER BY created_at DESC");
            }
        }

        String sqlQuery = queryBuilder.toString();

        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlQuery)) {

            int index = 1;

            if (keyword != null && !keyword.isEmpty()) {
                ps.setString(index++, "%" + keyword + "%");
                ps.setString(index++, "%" + keyword + "%");
            }
            if (minPrice != null) {
                ps.setInt(index++, minPrice);
            }
            if (maxPrice != null) {
                ps.setInt(index++, maxPrice);
            }
            if (minYear != null) {
                ps.setInt(index++, minYear);
            }
            if (maxYear != null) {
                ps.setInt(index++, maxYear);
            }
            if (mileage != null) {
                ps.setInt(index++, mileage);
            }
            if (fuelType != null && !fuelType.isEmpty()) {
                ps.setString(index++, fuelType);
            }
            if (location != null && !location.isEmpty()) {
                ps.setString(index++, "%" + location + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(mapRowToVehicle(rs));
                }
            } catch (SQLException e) {
                System.out.println("SQL Exception occurred when processing results.");
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.out.println("SQL Exception occurred when preparing or executing SQL.");
            e.printStackTrace();
        }

        return vehicles;
    }

    @Override
    public List<Vehicle> getAllVehiclesWithImages() {
        List<Vehicle> vehicles = new ArrayList<>();
        Connection conn = super.getConnection();

        String sql = "SELECT * FROM Vehicles";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Vehicle vehicle = mapRowToVehicle(rs);

                vehicle.setImage_url(getVehicleImages(vehicle.getId()));

                vehicles.add(vehicle);
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception occurred when executing SQL or processing results.");
            e.printStackTrace();
        } finally {
            super.freeConnection(conn);
        }

        return vehicles;
    }

    private List<String> getVehicleImages(Long vehicleId) {
        List<String> imageUrls = new ArrayList<>();
        Connection conn = super.getConnection();

        String sql = "SELECT image_url FROM vehicleimages WHERE vehicle_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, vehicleId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    imageUrls.add(rs.getString("image_url"));
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception occurred while fetching vehicle images.");
            e.printStackTrace();
        } finally {
            super.freeConnection(conn);
        }

        return imageUrls;
    }


}
