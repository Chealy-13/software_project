package org.example.software_project.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {
    private Long id;
    private Long userId;
    private Long vehicleId;
}
