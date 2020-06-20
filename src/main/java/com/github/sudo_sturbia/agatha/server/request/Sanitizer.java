package com.github.sudo_sturbia.agatha.server.request;

public class Sanitizer
{
    /**
     * Replace spaces in given string with underscores so it can
     * be used for table names.
     * @param str string to sanitize.
     * @return A sanitized string.
     */
    public static String sanitize(String str)
    {
        return str.replaceAll("\\s", "_");
    }
}
