package org.example.software_project.Persistence;

import org.example.software_project.business.Vehicle;

import java.util.List;

public interface FavoriteDao {
    void addFavorite(Long userId, Long vehicleId);
    void removeFavorite(Long userId, Long vehicleId);
    List<Vehicle> getFavoritesByUser(Long userId);
}
