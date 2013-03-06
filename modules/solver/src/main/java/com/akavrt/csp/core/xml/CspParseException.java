package com.akavrt.csp.core.xml;

/**
 * <p>Used to indicate failure when parsing XML input.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class CspParseException extends Exception {

    public CspParseException(String s) {
        super(s);
    }

    public CspParseException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public CspParseException(Throwable throwable) {
        super(throwable);
    }
}
