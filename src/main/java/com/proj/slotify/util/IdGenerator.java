package com.proj.slotify.util;

import java.security.SecureRandom;
import java.util.Locale;

public class IdGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TOTAL_LENGTH = 8;
    private static final int RANDOM_DIGITS_LENGTH = 5;

    public static String generateUserId(String name){
        if(name == null || name.isBlank()){
            return generateRandom("USR");
        }

        String prefix = name.trim()
                .toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z]", "")
                .substring(0, Math.min(3, name.trim().length()));

        while(prefix.length() < 3) {
            prefix += "X";
        }

        return prefix + generateRandomDigits();
    }

    public static String generateForAvailability() {
        return "AVL" + generateRandomDigits();
    }

    public static String generateForBooking() {
        return "BKG" + generateRandomDigits();
    }

    private static String generateRandomDigits(){
        return String.format("%0" + RANDOM_DIGITS_LENGTH + "d", RANDOM.nextInt((int) Math.pow(10, RANDOM_DIGITS_LENGTH)));
    }

    private static String generateRandom(String prefix){
        return prefix + generateRandomDigits();
    }
}
