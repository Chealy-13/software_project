package org.example.software_project.Persistence;

import org.example.software_project.business.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDaoImplRegisterTest {

    private static UserDaoImpl userDao;

    @BeforeEach
    public void setUp() {
        userDao = new UserDaoImpl();
    }

    @Test
    @Order(1)
    public void testRegisterValidUser() {
        User newUser = User.builder()
                .username("testUser")
                .firstName("Mark")
                .secondName("Smith")
                .email("testuser@gmail.com")
                .password("GoodPassword123")
                .phone("0854796584")
                .profilePicture(null)
                .build();

        boolean result = userDao.register(newUser);
        assertTrue(result, "User registration should be successful.");
    }

    @Test
    @Order(2)
    public void testRegisterDuplicateUser() {
        User duplicateUser = User.builder()
                .username("testUser")  // im using the same username as previous test
                .firstName("Mark")
                .secondName("Smith")
                .email("testuser@gmail.com")
                .password("GoodPassword123")
                .phone("0854796584")
                .profilePicture(null)
                .build();

        boolean result = userDao.register(duplicateUser);
        assertFalse(result, "Duplicate user registration should fail.");
    }

    @Test
    @Order(3)
    public void testRegisterUserWithInvalidEmail() {
        User invalidUser = User.builder()
                .username("invalidUser")
                .firstName("Invalid")
                .secondName("User")
                .email("invalid-email")
                .password("goodPassword123")
                .phone("0897415236")
                .profilePicture(null)
                .build();

        boolean result = userDao.register(invalidUser);
        assertFalse(result, "User with an invalid email should not be registered.");
    }

    @Test
    @Order(4)
    public void testRegisterUserWithWeakPassword() {
        User weakPasswordUser = User.builder()
                .username("weakUser123")
                .firstName("Brian")
                .secondName("Daly")
                .email("bdaly@gmail.com")
                .password("12345678")
                .phone("0834573363")
                .profilePicture(null)
                .build();

        boolean result = userDao.register(weakPasswordUser);
        assertFalse(result, "User with a weak password should not be registered.");
    }

    @Test
    @Order(5)
    public void testRegisterUserWithEmptyFields() {
        User emptyUser = User.builder()
                .username("")
                .firstName("")
                .secondName("")
                .email("")
                .password("")
                .phone("")
                .profilePicture(null)
                .build();

        boolean result = userDao.register(emptyUser);
        assertFalse(result, "User with empty fields should not be registered.");
    }

    @Test
    @Order(6)
    public void testRegisterUserWithLongUsername() {
        User longUsernameUser = User.builder()
                .username("Ihavealongusername")
                .firstName("Tom")
                .secondName("Brown")
                .email("longusername@gmail.com")
                .password("GoodPassword123")
                .phone("0875482236")
                .profilePicture(null)
                .build();

        boolean result = userDao.register(longUsernameUser);
        assertFalse(result, "User with a too-long username should not be registered.");
    }



}