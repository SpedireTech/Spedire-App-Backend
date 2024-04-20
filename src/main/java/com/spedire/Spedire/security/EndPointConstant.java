package com.spedire.Spedire.security;

import java.util.List;

public class EndPointConstant {

    public static final List<String> UNAUTHORIZEDENDPOINTS = List.of("/api/v1/sms/verifyOtp", "/api/v1/user/verifyPhoneNumber", "/api/v1/user/completeRegistration", "/login", "/api/v1/user/forgotPassword", "/api/v1/user/resetPassword");

}
