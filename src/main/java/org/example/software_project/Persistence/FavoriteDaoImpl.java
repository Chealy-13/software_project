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

    public FavoriteDaoImpl(String propertiesFilename) {
        super(propertiesFilename);
    }

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
                images.isEmpty() ? null : images.get(0)
        );
    }


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

}
