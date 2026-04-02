package edu.pte.ttk.istallo_kezelo.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity representing an application user account and role.
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
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

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Horse> ownedHorses = new ArrayList<>();
}
