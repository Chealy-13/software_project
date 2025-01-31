package org.example.software_project.business;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private int id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private int role;
    private String profile_picture;
    private LocalDateTime createdAt;

}
