package org.example.software_project.Persistence;

import org.example.software_project.business.Vehicle;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class VehicleDaoImpl extends MySQLDao implements VehicleDao{

    /**
     * Gets a list of all vehicles stored in the database.
     * It establishes a connection, executes an SQL query to fetch all vehicle records,
     * and maps the results to Vehicle objects.
     *
     * @return A list of Vehicle objects representing all vehicles in the database.
     * Returns an empty list if no vehicles are found.
     */
    @Override
    public List<Vehicle> getAllVehicles(){
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
     * Maps a row from the ResultSet to a Vehicle object.
     *
     * @param rs the ResultSet containing the vehicle data.
     * @return A Vehicle object populated with data from the current row of the result set.
     * @throws SQLException If an SQL error occurs while retrieving values from the result set.
     */
    private Vehicle mapRowToVehicle(ResultSet rs) throws SQLException {
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
}
