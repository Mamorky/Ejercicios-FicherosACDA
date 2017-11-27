package com.example.mamorky.ejerciciosdeficherosacda;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mamorky on 9/11/17.
 */

public class ValidatorUtil {
    private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final String PATTERN_NAME = "^[A-Za-z0-9]+.{3,20}";

    private static final String PATTERN_TLF = "^[0-9]{9}";

    /**
     * Validate given email with regular expression.
     *
     * @param email
     *            email for validation
     * @return true valid email, otherwise false
     */
    public static boolean validateEmail(String email) {

        Pattern pattern = Pattern.compile(PATTERN_EMAIL);

        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }

    public static boolean validateName(String name) {
        Pattern pattern = Pattern.compile(PATTERN_NAME);

        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    public static boolean validateTLF(String tlf) {
        Pattern pattern = Pattern.compile(PATTERN_TLF);

        Matcher matcher = pattern.matcher(tlf);
        return matcher.matches();
    }
}
