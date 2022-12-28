package com.javafxstore.user;

/**
 * User Class
 * Getters and setters used in UserController
 * @version 0.1
 */
public class User {

    private String name;
    private String username;
    private String password;
    private String role;

    /**
     * This is the default constructor
     */
    public User() {
        this("", "", "", "");
    }

    /**
     * This is the overloaded constructor
     * @param name - The name of the user
     * @param username - The username of the user
     * @param password - The password of the user
     * @param role - The user's role
     */
    public User(String name, String username, String password, String role) {
        super();
        this.name = (name);
        this.username =  (username);
        this.password =   (password);
        this.role =  (role);
    }

    /**
     * This is a Getter
     * @return name - User's name
     */
    public String getName() { return name; }

    /**
     * This is a Setter
     * @param name - User's Name
     */
    public void setName(String name) { this.name = (name); }

    /**
     * This is a Getter
     * @return username - User's username
     */
    public String getUsername() { return username; }

    /**
     * This is a Setter
     * @param username - User's username
     */
    public void setUsername(String username) { this.username = (username); }

    /**
     * This is a Getter
     * @return password - User's password
     */
    public String getPassword() { return password; }

    /**
     * This is a Setter
     * @param password - User's password
     */
    public void setPassword(String password) { this.password = (password); }

    /**
     * This is a Getter
     * @return role - User's role
     */
    public String getRole(){ return role; }

    /**
     * This is a Setter
     * @param role - User's role
     */
    public void setRole(String role) { this.role = (role); }

    /**
     * This function receives a user's username, password and role. It then checks to see if those parameters
     * equal the actual users' info.
     * @param username - This is the user's username
     * @param password - This is the user's password
     * @param role - This is the user's role
     * @return - returns either true or false depending on if the "if" statement is correct.
     */
    public boolean ValidateLogin(String username, String password, String role) {
        return ((this.username).toString().equals(username) && (this.password).toString().equals(password) && (this.role).toString().equals(role));
    }
}
