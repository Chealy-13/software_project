package org.example.software_project.Persistence;

import org.example.software_project.business.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

    @InjectMocks
    private UserDaoImpl userDao;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(mockConnection.prepareStatement(any())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Test
    void testLogin_Successful() throws Exception {
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("username")).thenReturn("damian2002");
        when(mockResultSet.getString("firstName")).thenReturn("Damian");
        when(mockResultSet.getString("secondName")).thenReturn("Magiera");
        when(mockResultSet.getString("email")).thenReturn("test@example.com");
        when(mockResultSet.getString("password")).thenReturn("password123");
        when(mockResultSet.getString("phone")).thenReturn("1234567890");
        when(mockResultSet.getString("profile_picture")).thenReturn("profile.jpg");
        when(mockResultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

        User user = userDao.login("damian2002", "password123");

        assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("damian2002", user.getUsername());
        assertEquals("Damian", user.getFirstName());
        assertEquals("Magiera", user.getSecondName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("1234567890", user.getPhone());
        assertEquals("profile.jpg", user.getProfilePicture());
    }

    @Test
    void testLogin_Failure_UserNotFound() throws Exception {
        when(mockResultSet.next()).thenReturn(false);

        User user = userDao.login("invalidUser", "wrongPassword");
        assertNull(user);
    }
}