package com.spedire.Spedire.security;

import java.util.List;

public class EndPointConstant {

    public static final List<String> UNAUTHORIZEDENDPOINTS = List.of("/api/v1/user/sign-up", "/api/v1/user/testing",
            "/api/v1/user/verifyPhoneNumber", "/api/v1/sms/verify-otp", "/api/v1/otp/verifyOtp", "swagger-ui.html", "swagger-ui/index.html");

}
