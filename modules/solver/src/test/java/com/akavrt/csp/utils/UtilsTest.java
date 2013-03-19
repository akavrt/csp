package com.akavrt.csp.utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: akavrt
 * Date: 20.03.13
 * Time: 00:44
 */
public class UtilsTest {
    @Test
    public void stringEmpty() {
        assertTrue(Utils.isEmpty(null));
        assertTrue(Utils.isEmpty(""));
        assertFalse(Utils.isEmpty(" "));
        assertFalse(Utils.isEmpty("text"));
    }


}
