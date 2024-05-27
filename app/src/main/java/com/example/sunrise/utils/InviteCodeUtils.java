package com.example.sunrise.utils;

import java.util.Random;

public class InviteCodeUtils {
    public static String generateInviteCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000; // ensures a six-digit number
        return String.valueOf(code);
    }
}
