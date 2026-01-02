package edu.pte.ttk.istallo_kezelo.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import edu.pte.ttk.istallo_kezelo.model.enums.*;
import jakarta.persistence.*;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "user_lname", nullable = false)
    private String userLname;

    @Column(name = "user_fname", nullable = false)
    private String userFname;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone", nullable = true)
    private String phone;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;


    // One-to-many relationship with Horse (owned horses)
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Horse> ownedHorses = new ArrayList<>();

    // Constructors, getters, and setters

    //Getters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getUserLname() {
        return userLname;
    }

    public String getUserFname() {
        return userFname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public UserType getUserType() {
        return userType;
    }

    public String getPassword() {
        return password;
    }

    public List<Horse> getOwned_horses() {
        return ownedHorses;
    }

    //Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserLname(String userLname) {
        this.userLname = userLname;
    }

    public void setUserFname(String userFname) {
        this.userFname = userFname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

}
