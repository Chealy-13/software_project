package org.example.software_project.Persistence;

import org.example.software_project.business.User;
import org.example.software_project.business.Vehicle;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class VehicleDaoImpl extends MySQLDao implements VehicleDao {

    public VehicleDaoImpl() {
        super();
    }

    public VehicleDaoImpl(String propertiesFilename) {
        super(propertiesFilename);
    }

    /**
     * Saves a new vehicle listing to the database.
     *
     * @param vehicle The vehicle object containing details to be stored.
     */
    @Override
    public void saveVehicle(Vehicle vehicle, String fileName) {
        String sql = "INSERT INTO Vehicles (seller_id, make, model, year, price, mileage, fuel_type, transmission, category, description, location, status, image_url) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

            String imageUrl = (fileName != null && !fileName.isEmpty()) ? "/images/" + fileName : null;
            ps.setString(13, imageUrl);

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
        List<String> images = getVehicleImages(rs.getLong("id"));

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
                images,
                images.isEmpty() ? null : images.get(0),
                rs.getBoolean("flagged")
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

    /**
     * Retrieves a list of vehicles listed by a specific seller.
     * it queries the database to fetch all vehicles where the `seller_id`
     * matches the provided seller's ID. Each vehicle is mapped from the result set
     * into a `Vehicle` object and added to the list.
     *
     * @param sellerId The ID of the seller whose vehicles are being retrieved.
     * @return A list of `Vehicle` objects associated with the specified seller.
     * If no vehicles are found, returns an empty list.
     */
    @Override
    public List<Vehicle> getVehiclesBySeller(int sellerId) {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE seller_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    List<String> images = getVehicleImages(rs.getLong("id"));

                    Vehicle vehicle = new Vehicle(
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
                            images, // List of image URLs
                            images.isEmpty() ? null : images.get(0),
                            rs.getBoolean("flagged")
                    );

                    vehicles.add(vehicle);
                }
            }
        } catch (SQLException e) {
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

    @Override
    public void deleteVehicle(Long listingId) {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, listingId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateVehicle(Long id, String make, String model, double price) {
        String sql = "UPDATE vehicles SET make = ?, model = ?, price = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, make);
            ps.setString(2, model);
            ps.setDouble(3, price);
            ps.setLong(4, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Vehicle getVehicleById(Long vehicleId) {
        String sql = "SELECT * FROM vehicles WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, vehicleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
                            getVehicleImages(rs.getLong("id")),
                            null, //setting img url to null just to match cnstructor.
                            rs.getBoolean("flagged")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setFlagStatus(Long listingId, boolean flagged) {
        String sql = "UPDATE vehicles SET flagged = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, flagged);
            ps.setLong(2, listingId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


