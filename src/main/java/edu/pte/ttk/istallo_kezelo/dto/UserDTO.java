package edu.pte.ttk.istallo_kezelo.dto;

import edu.pte.ttk.istallo_kezelo.model.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String username;
    private String userLname;
    private String userFname;
    private String email;
    private String phone;
    private UserType userType;
    private Long userId;
}
