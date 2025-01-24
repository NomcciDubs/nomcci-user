package com.nomcci.user.management.model;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String country;
    private String city;
}
