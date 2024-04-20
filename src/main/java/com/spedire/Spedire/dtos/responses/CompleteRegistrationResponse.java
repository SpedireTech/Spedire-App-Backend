package com.spedire.Spedire.dtos.responses;

import lombok.*;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class CompleteRegistrationResponse {

    private String message;
    private boolean success;
    private RegistrationResponse data;



    @Getter
    @Setter
    @AllArgsConstructor
    @RequiredArgsConstructor
    @Builder
    public static class RegistrationResponse {
        private String firstName;
        private String lastName;
        private String image;
    }


}
