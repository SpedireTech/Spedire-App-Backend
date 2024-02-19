package com.spedire.Spedire.models;


import com.spedire.Spedire.enums.Role;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Document
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class User {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String password;

    @Indexed(unique = true)
    private String phoneNumber;
    private String profileImage;

    @Indexed(unique = true)
    private String email;


    private List<Role> roles = new ArrayList<>();

    public User(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
