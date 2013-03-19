package com.akavrt.csp.utils;

/**
 * <p>Collection of utility methods.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class Utils {

    /**
     * <p>Returns true if the string is null or 0-length.</p>
     *
     * @param str The string to be examined.
     * @return true if str is null or zero length.
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * <p>Convert array of integers into string representation.</p>
     *
     * <p>Following template is used: '[CAPTION:] [ item[0] item[1] ... item[n] ]'</p>
     *
     * @param name  Caption, will be added before content of the array.
     * @param array Array of integers to be converted.
     * @return String representation of the array, preceded by caption.
     */
    public static String convertIntArrayToString(String name, int[] array) {
        StringBuilder builder = new StringBuilder();
        if (!Utils.isEmpty(name)) {
            builder.append(name);
            builder.append(": ");
        }

        if (array == null) {
            builder.append("null");
        } else {
            builder.append("[ ");
            for (int i : array) {
                builder.append(i);
                builder.append(" ");
            }
            builder.append("]");
        }

        return builder.toString();
    }


}
