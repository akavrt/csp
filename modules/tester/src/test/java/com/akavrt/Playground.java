package com.akavrt;

import com.google.common.base.Objects;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: akavrt
 * Date: 29.03.13
 * Time: 17:57
 */
public class Playground {

    @Test
    public void checkHash() {
        int[] pattern1 = new int[] {0, 1, 1};
        int hash1reference = Objects.hashCode(pattern1);
        int hash1content = Arrays.hashCode(pattern1);

        int[] pattern2 = new int[] {0, 1, 1};
        int hash2reference = Objects.hashCode(pattern2);
        int hash2content = Arrays.hashCode(pattern2);

        assertFalse(hash1reference == hash2reference);
        assertTrue(hash1content == hash2content);

        int[] pattern3 = new int[] {2, 1, 1};
        int hash3content = Arrays.hashCode(pattern3);

        assertFalse(hash3content == hash1content);
        assertFalse(hash3content == hash2content);
    }

    @Test
    public void division() {
        double size = 0;
        size += 2;
        size += 3;

        assertTrue(size / 2 > 2);

        int length = 0;
        length += 2;
        length += 3;

        assertTrue(length / 2 == 2);
    }
}
