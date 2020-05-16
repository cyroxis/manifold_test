package com.mt;

public class PrintUtil {

    public static void print(String message) {
        System.out.println("[DEBUG] " + message);
    }

    public static void print(String formant, Object... args) {
        print(String.format(formant, args));
    }
}
