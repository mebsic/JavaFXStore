package com.javafxstore.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    private static User user;

    @BeforeEach
    void setUp() {
        user = new User("test@example.com", "TestUsername", "TestPassword","admin");
    }

    @Test
    public void testGetName() {
        String name = "test@example.com";
        assertEquals(name, user.getName());
    }

    @Test
    public void testGetUsername() {
        String username = "TestUsername";
        assertEquals(username, user.getUsername());
    }

    @Test
    public void testGetPassword() {
        String password = "TestPassword";
        assertEquals(password, user.getPassword());
    }

    @Test
    public void testGetRole() {
        String role = "admin";
        assertEquals(role, user.getRole());
    }


    @Test
    public void testSetName() {
        String name = "test@example.com";
        user.setName("test@example.com");
        assertEquals(name, user.getName());
    }

    @Test
    public void testSetUsername() {
        String username = "TestUsername";
        user.setUsername("TestUsername");
        assertEquals(username, user.getUsername());
    }

    @Test
    public void testSetPassword() {
        String password = "TestPassword";
        user.setPassword("TestPassword");
        assertEquals(password, user.getPassword());
    }

    @Test
    public void testSetRole() {
        String role = "user";
        user.setRole("user");
        assertEquals(role, user.getRole());
    }

    @Test
    public void testValidateLogin() {
        String username = "TestUsername";
        String password = "TestPassword";
        String role = "admin";

        Assertions.assertEquals(username, user.getUsername());
        Assertions.assertEquals(password, user.getPassword());
        Assertions.assertEquals(role, user.getRole());
    }
}
