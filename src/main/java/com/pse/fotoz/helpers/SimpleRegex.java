package com.pse.fotoz.helpers;

import java.util.regex.Pattern;

/**
 * Class simplifying regex applications.
 * Because Java doesn't.
 * @author Robert
 */
public class SimpleRegex {
    public static boolean matches(String regex, String subject) {
        return Pattern.compile(regex).matcher(subject).find();
    }
}
