package org.example.software_project.Persistence;


import org.example.software_project.business.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.sql.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDaoImplTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    private UserDao UserDao;


    @Test
    void testLoginSuccessful() throws SQLException {
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("testUser");
        when(mockResultSet.getString("email")).thenReturn("test@example.com");
        when(mockResultSet.getString("password")).thenReturn("password123");
        when(mockResultSet.getString("phone")).thenReturn("1234567890");
        when(mockResultSet.getString("profile_picture")).thenReturn("profile.jpg");
        when(mockResultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

        User result = UserDao.login("testUser", "password123");

        assertNotNull(result);
        assertEquals("testUser", result.getName());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testLoginFailedNoUserFound() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);
        User result = UserDao.login("nonexistentUser", "password123");
        assertNull(result);
    }

}
