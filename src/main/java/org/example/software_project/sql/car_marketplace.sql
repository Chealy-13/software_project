DROP DATABASE IF EXISTS car_marketplace;

CREATE DATABASE car_marketplace;

USE car_marketplace;

CREATE TABLE `advertisements` (
                                  `id` int(11) NOT NULL,
                                  `vehicle_id` int(11) NOT NULL,
                                  `seller_id` bigint(20) NOT NULL,
                                  `ad_price` decimal(10,2) NOT NULL,
                                  `status` enum('Pending','Active','Expired','Cancelled') DEFAULT 'Pending',
                                  `start_date` timestamp NOT NULL DEFAULT current_timestamp(),
                                  `end_date` timestamp NULL DEFAULT NULL,
                                  `is_featured` tinyint(1) DEFAULT 0,
                                  `payment_id` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `favorites`
--

CREATE TABLE `favorites` (
                             `id` int(11) NOT NULL,
                             `user_id` int(11) NOT NULL,
                             `vehicle_id` int(11) NOT NULL,
                             `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `favorites`
--

INSERT INTO `favorites` (`id`, `user_id`, `vehicle_id`, `created_at`) VALUES
                                                                          (3, 25, 8, '2025-04-05 23:35:03'),
                                                                          (4, 25, 9, '2025-04-05 23:35:09'),
                                                                          (5, 26, 8, '2025-04-07 23:25:02'),
                                                                          (6, 26, 9, '2025-04-07 23:25:10'),
                                                                          (7, 25, 7, '2025-04-24 14:23:45');

-- --------------------------------------------------------

--
-- Table structure for table `reviews`
--

CREATE TABLE `reviews` (
                           `id` int(11) NOT NULL,
                           `user_id` int(11) NOT NULL,
                           `seller_id` int(11) NOT NULL,
                           `rating` int(11) DEFAULT NULL CHECK (`rating` >= 1 and `rating` <= 5),
                           `comment` text DEFAULT NULL,
                           `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
                         `id` bigint(20) NOT NULL,
                         `username` varchar(100) NOT NULL,
                         `firstName` varchar(100) NOT NULL,
                         `secondName` varchar(100) NOT NULL,
                         `email` varchar(100) NOT NULL,
                         `password` varchar(255) NOT NULL,
                         `phone` varchar(20) NOT NULL,
                         `role` tinyint(4) NOT NULL DEFAULT 1,
                         `profile_picture` varchar(255) DEFAULT NULL,
                         `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
                         `reset_token` varchar(255) DEFAULT NULL,
                         `token_expiry` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `firstName`, `secondName`, `email`, `password`, `phone`, `role`, `profile_picture`, `created_at`, `reset_token`, `token_expiry`) VALUES
                                                                                                                                                                            (15, 'HashTester', 'Hash', 'Tester', 'htester@gmail.com', '$2a$10$hB38lg8BKSF2rCYxrbArOOXjaErTKCnls0k.EDuiF/LmCreDuj5vO', '0856213348', 2, NULL, '2025-02-10 15:14:39', NULL, NULL),
                                                                                                                                                                            (20, 'test22222222222', 'test2', 'test2', 'test2@mail.com', '$2a$10$WtMYM21AiDgjyPhhZwjzI.08mRHYld8JbcEQK4djsF8NOkeKUG.V2', '1234567899', 1, NULL, '2025-03-03 13:36:30', NULL, NULL),
                                                                                                                                                                            (21, 'admin', 'test3', 'test3', 'admin@mail.com', '$2a$10$B4AXN1k20BXQs7Erl1e42eG0FReKZMvqCw2rBm3VQxWIf2CYTkrOi', '1234567898', 3, NULL, '2025-03-10 15:45:11', NULL, NULL),
                                                                                                                                                                            (25, 'test4', 'test', 'test', 'test4@mail.com', '$2a$10$kNV0KUMwcq26OFiz5CfMq.Po0Z4J.aCeINzEkYbUCIJtD2yvOsNq.', '1234567898', 1, NULL, '2025-04-05 23:34:08', NULL, NULL),
                                                                                                                                                                            (26, 'test5', 'test5', 'test5', 'test5@mail.com', '$2a$10$wbXgZFNDtpwGWfHXlsHmkuTimvz5boSaQrpiZWjyEJ/pzdhfDsYsK', '1234567898', 2, NULL, '2025-04-07 23:24:52', NULL, NULL),
                                                                                                                                                                            (27, 'newUser1', 'jim', 'bob', 'newUser@mail.com', '$2a$10$AcujecVPILwPpwfRF76IxOr1jn1qtCF4PansHpJwJYvJ09ABGPZ.W', '1234567898', 1, NULL, '2025-04-26 14:04:18', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `vehicleimages`
--

CREATE TABLE `vehicleimages` (
                                 `id` int(11) NOT NULL,
                                 `vehicle_id` int(11) NOT NULL,
                                 `image_url` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `vehicleimages`
--

INSERT INTO `vehicleimages` (`id`, `vehicle_id`, `image_url`) VALUES
                                                                  (1, 7, '/images/toyotacorolla.png'),
                                                                  (2, 8, '/images/hondacivic.png'),
                                                                  (3, 9, '/images/bmw.png'),
                                                                  (4, 10, '/images/tesla.png'),
                                                                  (5, 11, '/images/fordfocus.png'),
                                                                  (6, 12, '/images/mercedesaclass.png'),
                                                                  (7, 11, '/images/insideford.png'),
                                                                  (8, 12, '/images/insidemercedes.png'),
                                                                  (9, 10, '/images/insidetesla.png'),
                                                                  (10, 7, '/images/insidetoyota.png'),
                                                                  (11, 8, '/images/insidehonda.png'),
                                                                  (12, 9, '/images/insidebmw.png'),
                                                                  (13, 11, '/images/fordseats.png'),
                                                                  (14, 7, '/images/corollaseats.png'),
                                                                  (16, 9, '/images/bmwseats.png'),
                                                                  (17, 10, '/images/teslaseats.png'),
                                                                  (18, 12, '/images/mercedesseats.png');

-- --------------------------------------------------------

--
-- Table structure for table `vehicles`
--

CREATE TABLE `vehicles` (
                            `id` int(11) NOT NULL,
                            `seller_id` int(11) NOT NULL,
                            `make` varchar(50) NOT NULL,
                            `model` varchar(100) NOT NULL,
                            `year` year(4) NOT NULL,
                            `price` decimal(10,2) NOT NULL,
                            `mileage` int(11) NOT NULL,
                            `fuel_type` enum('Petrol','Diesel','Electric','Hybrid') NOT NULL,
                            `transmission` enum('Manual','Automatic') NOT NULL,
                            `category` enum('Saloon','SUV','Estate','Coupe','Hatchback','MPV','Van','Convertible','Pick Up') NOT NULL,
                            `description` text DEFAULT NULL,
                            `location` varchar(255) DEFAULT NULL,
                            `status` enum('Available','Sold') DEFAULT 'Available',
                            `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
                            `image_url` varchar(255) DEFAULT NULL,
                            `flagged` tinyint(1) DEFAULT 0,
                            `favorite_count` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `vehicles`
--

INSERT INTO `vehicles` (`id`, `seller_id`, `make`, `model`, `year`, `price`, `mileage`, `fuel_type`, `transmission`, `category`, `description`, `location`, `status`, `created_at`, `image_url`, `flagged`, `favorite_count`) VALUES
                                                                                                                                                                                                                                  (7, 15, 'Toyota', 'Corolla', '2020', 23000.00, 15000, 'Petrol', 'Automatic', 'Saloon', 'A well-maintained Toyota Corolla in excellent condition.', 'Dublin, Ireland', 'Available', '2025-02-14 14:18:40', '/images/toyotacorolla.png', 1, 0),
                                                                                                                                                                                                                                  (8, 15, 'Honda', 'Civic', '2019', 19000.00, 30000, 'Diesel', 'Manual', 'Hatchback', 'Great condition, ideal for long drives and city commutes.', 'Cork, Ireland', 'Available', '2025-02-14 14:18:40', '/images/hondacivic.png', 1, 0),
                                                                                                                                                                                                                                  (9, 15, 'BMW', '320i', '2021', 35000.00, 8000, 'Petrol', 'Automatic', 'Saloon', 'Luxury sedan with a powerful engine and modern features.', 'Limerick, Ireland', 'Available', '2025-02-14 14:18:40', '/images/bmw.png', 1, 0),
                                                                                                                                                                                                                                  (10, 15, 'Tesla', 'Model 3', '2022', 45000.00, 5000, 'Electric', 'Automatic', 'Saloon', 'An eco-friendly electric car with advanced autopilot features.', 'Galway, Ireland', 'Available', '2025-02-14 14:18:40', '/images/tesla.png', 1, 0),
                                                                                                                                                                                                                                  (11, 15, 'Ford', 'Focus', '2018', 15000.00, 45000, 'Diesel', 'Manual', 'Hatchback', 'A reliable car with great fuel efficiency.', 'Belfast, Northern Ireland', 'Sold', '2025-02-14 14:18:40', '/images/fordfocus.png', 1, 0),
                                                                                                                                                                                                                                  (12, 15, 'Mercedes', 'A-Class', '2020', 27000.00, 20000, 'Petrol', 'Automatic', 'Hatchback', 'Compact luxury car with great handling and performance.', 'Cork, Ireland', 'Available', '2025-02-14 14:18:40', '/images/mercedesaclass.png', 0, 0);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `advertisements`
--
ALTER TABLE `advertisements`
    ADD PRIMARY KEY (`id`),
    ADD KEY `vehicle_id` (`vehicle_id`),
    ADD KEY `seller_id` (`seller_id`);

--
-- Indexes for table `favorites`
--
ALTER TABLE `favorites`
    ADD PRIMARY KEY (`id`),
    ADD KEY `user_id` (`user_id`),
    ADD KEY `vehicle_id` (`vehicle_id`);

--
-- Indexes for table `reviews`
--
ALTER TABLE `reviews`
    ADD PRIMARY KEY (`id`),
    ADD KEY `user_id` (`user_id`),
    ADD KEY `seller_id` (`seller_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
    ADD PRIMARY KEY (`id`),
    ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `vehicleimages`
--
ALTER TABLE `vehicleimages`
    ADD PRIMARY KEY (`id`),
    ADD KEY `vehicle_id` (`vehicle_id`);

--
-- Indexes for table `vehicles`
--
ALTER TABLE `vehicles`
    ADD PRIMARY KEY (`id`),
    ADD KEY `seller_id` (`seller_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `advertisements`
--
ALTER TABLE `advertisements`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `favorites`
--
ALTER TABLE `favorites`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `reviews`
--
ALTER TABLE `reviews`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=28;

--
-- AUTO_INCREMENT for table `vehicleimages`
--
ALTER TABLE `vehicleimages`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `vehicles`
--
ALTER TABLE `vehicles`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `advertisements`
--
ALTER TABLE `advertisements`
    ADD CONSTRAINT `advertisements_ibfk_1` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`id`) ON DELETE CASCADE,
    ADD CONSTRAINT `advertisements_ibfk_2` FOREIGN KEY (`seller_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `favorites`
--
ALTER TABLE `favorites`
    ADD CONSTRAINT `favorites_ibfk_2` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `vehicleimages`
--
ALTER TABLE `vehicleimages`
    ADD CONSTRAINT `vehicleimages_ibfk_1` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`id`) ON DELETE CASCADE;
COMMIT;
