package com.spedire.Spedire.security;

import java.util.List;

public class EndPointConstant {

    public static final List<String> UNAUTHORIZEDENDPOINTS = List.of("/api/v1/sms/verify-otp", "/api/v1/user/savePhoneNumber", "/api/v1/user/complete-registration", "/login");

}
