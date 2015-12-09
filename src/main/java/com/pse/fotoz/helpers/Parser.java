package com.pse.fotoz.helpers;

import java.util.Optional;

/**
 *
 * @author Robert
 */
public class Parser {
    
    /**
     * Attempts to parse an integer from a string.
     * @param integer A string representing an integer.
     * @return Maybe an integer if it can be parsed, otherwise nothing.
     */
    public static Optional<Integer> parseInt(String integer) {
        try {
            return Optional.of(Integer.parseInt(integer));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
    
    /**
     * Attempts to parse a double from a string.
     * @param real A string representing a double.
     * @return Maybe a double if it can be parsed, otherwise nothing.
     */
    public static Optional<Double> parseDouble(String real) {
        try {
            return Optional.of(Double.parseDouble(real));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
    
}
