package com.spedire.Spedire.security;

import java.util.List;

public class EndPointConstant {

    public static final List<String> UNAUTHORIZEDENDPOINTS = List.of("/api/v1/user/test", "/api/v1/user/verifyPhoneNumberFirst/**", "/login");

}
