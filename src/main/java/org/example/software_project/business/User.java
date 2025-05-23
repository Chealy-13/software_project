package org.example.software_project.business;


import lombok.*;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {


    private int id;
    private String username;
    private String firstName;
    private String secondName;
    private String email;
    private String password;
    private String phone;
    private String profilePicture;
    private LocalDateTime createdAt;
    private int role;
    private String resetToken;
    private LocalDateTime tokenExpiry;

}

