package com.akavrt.csp.core;

/**
 * <p>Used to indicate failure and what actually causes it when setting up problem.</p>
 *
 * <p>See ProblemBuilder for more details.</p>
 *
 * @author Victor balabanov <akavrt@gmail.com>
 */
public class IllegalProblemException extends RuntimeException {

    public IllegalProblemException(String s) {
        super(s);
    }

}
