package com.spedire.Spedire.dtos.requests;

import lombok.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@Getter
public class CompleteRegistrationRequest {

    @Email
    private String email;


    private String password;

    @NotBlank(message = "First name is required.")
    @Size(min = 2, message = "First name should have at least 2 letters.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(min = 2, message = "Last name should have at least 2 letters.")
    private String lastName;
    private String image;

}
