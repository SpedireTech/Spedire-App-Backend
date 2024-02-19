package com.spedire.Spedire.security;

import java.util.List;

public class EndPointConstant {

    public static final List<String> UNAUTHORIZEDENDPOINTS = List.of("/api/user/verifyPhoneNumberFirst/**", "/login");

}
