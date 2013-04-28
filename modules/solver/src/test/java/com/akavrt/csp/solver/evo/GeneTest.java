package com.akavrt.csp.solver.evo;

import com.akavrt.csp.core.Roll;
import com.akavrt.csp.solver.evo.Gene;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * User: akavrt
 * Date: 31.03.13
 * Time: 13:42
 */
public class GeneTest {

    @Test
    public void rollsFromSameGroup() {
        int[] pattern = new int[]{0, 1, 1};

        // two physically distinct rolls representing same group
        // of rolls (all rolls in a group has identical sizes)
        Roll roll1 = new Roll("roll1", 1, 300, 50);
        Roll roll2 = new Roll("roll1", 2, 300, 50);

        assertFalse(roll1.getInternalId() == roll2.getInternalId());
        assertEquals(roll1.getId(), roll2.getId());

        Gene gene1 = new Gene(pattern, roll1);
        Gene gene2 = new Gene(pattern, roll2);

        assertEquals(gene1.getPatternHashCode(), gene2.getPatternHashCode());

        // In terms of solution there is no difference which of the two rolls
        // is actually used if both of them represent same group: that's why
        // two genes from above has to have same hash code.
        assertEquals(gene1.hashCode(), gene2.hashCode());
    }

    @Test
    public void rollsFromDifferentGroups() {
        int[] pattern = new int[]{0, 1, 1};

        Roll roll1 = new Roll("roll1", 200, 40);
        Roll roll2 = new Roll("roll2", 300, 50);

        assertFalse(roll1.getInternalId() == roll2.getInternalId());
        assertFalse(roll1.getId() == roll2.getId());

        Gene gene1 = new Gene(pattern, roll1);
        Gene gene2 = new Gene(pattern, roll2);

        assertEquals(gene1.getPatternHashCode(), gene2.getPatternHashCode());
        assertFalse(gene1.hashCode() == gene2.hashCode());
    }

    @Test
    public void genesWithoutRoll() {
        int[] pattern = new int[]{0, 1, 1};

        Gene gene1 = new Gene(pattern, null);
        Gene gene2 = new Gene(pattern, null);

        assertEquals(gene1.getPatternHashCode(), gene2.getPatternHashCode());
        assertEquals(gene1.hashCode(), gene2.hashCode());
    }
}
