package edu.pte.ttk.istallo_kezelo.dto;

//import java.util.List;

import edu.pte.ttk.istallo_kezelo.model.UserType;


public class UserDTO {
    public String username;
    public String userLname;
    public String userFname;
    public String email;
    public String phone;
    //public List<HorseSummaryDTO> horses;
    public UserType userType;
    public Long id;

    public UserDTO() {
    }

    public UserDTO(String username, String lName, String fName, String email, String phone, /*List<HorseSummaryDTO> horses, */UserType userType){
        this.username = username;
        this.userLname = lName;
        this.userFname = fName;
        this.email = email;
        this.phone = phone;
        //this.horses = horses;
        this.userType = userType;
    }
}
