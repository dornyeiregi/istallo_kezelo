package edu.pte.ttk.istallo_kezelo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Adatátviteli objektum a(z) SignupRequest adatcseréhez.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDTO {
    private String username;
    @JsonProperty("lName")
    private String lName;
    @JsonProperty("fName")
    private String fName;
    private String email;
    private String phone;
    private UserType userType;
    private String password;
}
