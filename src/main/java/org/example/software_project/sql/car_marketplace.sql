DROP DATABASE IF EXISTS car_marketplace;

CREATE DATABASE car_marketplace;

USE car_marketplace;

CREATE TABLE Users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(100) NOT NULL,
                       firstName VARCHAR(100) NOT NULL,
                       secondName VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       phone VARCHAR(20) NOT NULL,
                       role INT NOT NULL DEFAULT 1, -- 1 = buyer, 2 = seller, 3 = admin
                       profile_picture VARCHAR(255),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Vehicles (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          seller_id INT NOT NULL,
                          make VARCHAR(50) NOT NULL,
                          model VARCHAR(100) NOT NULL,
                          year YEAR NOT NULL,
                          price DECIMAL(10, 2) NOT NULL,
                          mileage INT NOT NULL, -- not sure to stick to one or allow either km or miles
                          fuel_type ENUM('Petrol', 'Diesel', 'Electric', 'Hybrid') NOT NULL,
                          transmission ENUM('Manual', 'Automatic') NOT NULL,
                          category ENUM('Saloon', 'SUV', 'Estate', 'Coupe', 'Hatchback', 'MPV', 'Van', 'Convertible', 'Pick Up') NOT NULL,
                          description TEXT,
                          location VARCHAR(255), -- City or region
                          status ENUM('Available', 'Sold') DEFAULT 'Available',
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (seller_id) REFERENCES Users(id) ON DELETE CASCADE
);


CREATE TABLE VehicleImages (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               vehicle_id INT NOT NULL,
                               image_url VARCHAR(255) NOT NULL,
                               FOREIGN KEY (vehicle_id) REFERENCES Vehicles(id) ON DELETE CASCADE
);

CREATE TABLE Reviews (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         user_id INT NOT NULL,
                         seller_id INT NOT NULL,
                         rating INT CHECK (rating >= 1 AND rating <= 5),
                         comment TEXT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
                         FOREIGN KEY (seller_id) REFERENCES Users(id) ON DELETE CASCADE
);

CREATE TABLE Favorites (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           user_id INT NOT NULL,
                           vehicle_id INT NOT NULL,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
                           FOREIGN KEY (vehicle_id) REFERENCES Vehicles(id) ON DELETE CASCADE
);

CREATE TABLE Messages (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          sender_id INT NOT NULL,
                          receiver_id INT NOT NULL,
                          vehicle_id INT NOT NULL,
                          message TEXT NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (sender_id) REFERENCES Users(id) ON DELETE CASCADE,
                          FOREIGN KEY (receiver_id) REFERENCES Users(id) ON DELETE CASCADE,
                          FOREIGN KEY (vehicle_id) REFERENCES Vehicles(id) ON DELETE CASCADE
);

CREATE TABLE Advertisements (
                                id INT AUTO_INCREMENT PRIMARY KEY,
                                vehicle_id INT NOT NULL,
                                seller_id INT NOT NULL,
                                ad_price DECIMAL(10, 2) NOT NULL, -- Cost of the ad
                                status ENUM('Pending', 'Active', 'Expired', 'Cancelled') DEFAULT 'Pending',
                                start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                end_date TIMESTAMP NULL, -- NULL if not paid yet
                                is_featured BOOLEAN DEFAULT FALSE, -- Featured ads get more visibility
                                payment_id VARCHAR(100), -- to store payments for advertising
                                FOREIGN KEY (vehicle_id) REFERENCES Vehicles(id) ON DELETE CASCADE,
                                FOREIGN KEY (seller_id) REFERENCES Users(id) ON DELETE CASCADE
);
