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
public class FavoriteDaoImpl extends MySQLDao implements FavoriteDao{

    public FavoriteDaoImpl() {
        super();
    }

    /**
     * Constructor that accepts a custom properties filename for database connection.
     *
     * @param propertiesFilename the filename of the database properties file
     */
    public FavoriteDaoImpl(String propertiesFilename) {
        super(propertiesFilename);
    }

    /**
     * Adds a vehicle to the user's favorites list if it is not already added.
     *
     * @param userId    the ID of the user
     * @param vehicleId the ID of the vehicle to add
     */
    @Override
    public void addFavorite(Long userId, Long vehicleId) {
        if (!isFavorite(userId, vehicleId)) {
            String sql = "INSERT INTO favorites (user_id, vehicle_id) VALUES (?, ?)";

            try (Connection conn = super.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, userId);
                ps.setLong(2, vehicleId);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.out.println("SQL Exception while adding favorite.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Removes a vehicle from the user's favorites list.
     *
     * @param userId    the ID of the user
     * @param vehicleId the ID of the vehicle to remove
     */
    @Override
    public void removeFavorite(Long userId, Long vehicleId) {
        String sql = "DELETE FROM favorites WHERE user_id = ? AND vehicle_id = ?";

        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, vehicleId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL Exception while removing favorite.");
            e.printStackTrace();
        }
    }


    /**
     * Retrieves a list of favorite vehicles for the specified user.
     *
     * @param userId the ID of the user
     * @return a list of favorite vehicles
     */
    @Override
    public List<Vehicle> getFavoritesByUser(Long userId) {
        List<Vehicle> favoriteVehicles = new ArrayList<>();
        String sql = "    SELECT v.* FROM vehicles v\n" +
                     "    INNER JOIN favorites f ON v.id = f.vehicle_id\n" +
                     "    WHERE f.user_id = ?\n";

        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    favoriteVehicles.add(mapRowToVehicle(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception while retrieving favorites.");
            e.printStackTrace();
        }

        return favoriteVehicles;
    }

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
                rs.getBoolean("flagged"),
                rs.getInt("favorite_count")
        );
    }


    /**
     * Retrieves a list of image URLs associated with a specific vehicle.
     *
     * @param vehicleId the ID of the vehicle
     * @return a list of image URLs
     */
    private List<String> getVehicleImages(Long vehicleId) {
        List<String> imageUrls = new ArrayList<>();
        String sql = "SELECT image_url FROM vehicleimages WHERE vehicle_id = ?";

        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, vehicleId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    imageUrls.add(rs.getString("image_url"));
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception while retrieving vehicle images.");
            e.printStackTrace();
        }

        return imageUrls;
    }

    /**
     * Checks if a specific vehicle is already in the user's favorites.
     *
     * @param userId    the ID of the user
     * @param vehicleId the ID of the vehicle
     * @return true if the vehicle is a favorite, false otherwise
     */
    @Override
    public boolean isFavorite(Long userId, Long vehicleId) {
        String sql = "SELECT COUNT(*) FROM favorites WHERE user_id = ? AND vehicle_id = ?";

        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, vehicleId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception while checking favorite.");
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<Vehicle> getTopFavoritedVehiclesWithCounts() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = """
        SELECT v.*, COUNT(f.id) AS total_saves
        FROM vehicles v
        JOIN favorites f ON v.id = f.vehicle_id
        GROUP BY v.id
        ORDER BY total_saves DESC
        LIMIT 3
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Vehicle vehicle = mapRowToVehicle(rs);
                vehicle.setFavoriteCount(rs.getInt("total_saves"));
                vehicles.add(vehicle);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vehicles;
    }



}
