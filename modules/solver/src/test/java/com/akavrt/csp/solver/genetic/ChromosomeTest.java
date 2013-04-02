package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Roll;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: akavrt
 * Date: 31.03.13
 * Time: 14:33
 */
public class ChromosomeTest {
    private GeneticTestContext context;
    private Roll roll1;
    private Roll roll2_1;
    private Roll roll2_2;
    private Roll roll3;

    @Before
    public void setUpContext() {
        // preparing orders
        List<Order> orders = Lists.newArrayList();

        orders.add(new Order("order1", 500, 50));
        orders.add(new Order("order2", 400, 40));
        orders.add(new Order("order3", 300, 30));

        // preparing rolls
        List<Roll> rolls = Lists.newArrayList();

        roll1 = new Roll("roll1", 300, 200);
        roll2_1 = new Roll("roll2", 1, 500, 300);
        roll2_2 = new Roll("roll2", 2, 500, 300);
        roll3 = new Roll("roll3", 400, 100);

        rolls.add(roll1);
        rolls.add(roll2_1);
        rolls.add(roll2_2);
        rolls.add(roll3);

        Problem problem = new Problem(orders, rolls);
        context = new GeneticTestContext(problem);
    }

    @Test
    public void emptyChromosomes() {
        Chromosome ch1 = new Chromosome(context);
        Chromosome ch2 = new Chromosome(context);

        assertEquals(0, ch1.hashCode());
        assertEquals(0, ch2.hashCode());
        assertTrue(ch1.hashCode() == ch2.hashCode());
        assertTrue(ch1.equals(ch2));
        assertTrue(ch2.equals(ch1));
    }

    @Test
    public void oneGeneSamePatternsSameRoll() {
        Gene gene;

        gene = new Gene(new int[] {0, 1, 1}, roll1);
        Chromosome ch1 = new Chromosome(context);
        ch1.addGene(gene);

        gene = new Gene(new int[] {0, 1, 1}, roll1);
        Chromosome ch2 = new Chromosome(context);
        ch2.addGene(gene);

        assertEquals(ch1.hashCode(), ch2.hashCode());
        assertTrue(ch1.equals(ch2));
        assertTrue(ch2.equals(ch1));
    }

    @Test
    public void oneGeneSamePatternsRollsFromSameGroup() {
        Gene gene;

        gene = new Gene(new int[] {0, 1, 1}, roll2_1);
        Chromosome ch1 = new Chromosome(context);
        ch1.addGene(gene);

        gene = new Gene(new int[] {0, 1, 1}, roll2_2);
        Chromosome ch2 = new Chromosome(context);
        ch2.addGene(gene);

        assertEquals(ch1.hashCode(), ch2.hashCode());
        assertTrue(ch1.equals(ch2));
        assertTrue(ch2.equals(ch1));
    }

    @Test
    public void oneGeneSamePatternsDifferentRolls() {
        Gene gene;

        gene = new Gene(new int[] {0, 1, 1}, roll1);
        Chromosome ch1 = new Chromosome(context);
        ch1.addGene(gene);

        gene = new Gene(new int[] {0, 1, 1}, roll2_1);
        Chromosome ch2 = new Chromosome(context);
        ch2.addGene(gene);

        assertFalse(ch1.hashCode() == ch2.hashCode());
        assertFalse(ch1.equals(ch2));
        assertFalse(ch2.equals(ch1));
    }

    @Test
    public void twoGenesSamePatternsSameOrder() {
        Chromosome ch1 = new Chromosome(context);
        ch1.addGene(new Gene(new int[] {0, 1, 1}, roll1));
        ch1.addGene(new Gene(new int[] {2, 1, 1}, roll2_1));

        Chromosome ch2 = new Chromosome(context);
        ch2.addGene(new Gene(new int[] {0, 1, 1}, roll1));
        ch2.addGene(new Gene(new int[] {2, 1, 1}, roll2_1));

        assertEquals(ch1.hashCode(), ch2.hashCode());
        assertTrue(ch1.equals(ch2));
        assertTrue(ch2.equals(ch1));
    }

    @Test
    public void twoGenesSamePatternsDifferentOrder() {
        Chromosome ch1 = new Chromosome(context);
        ch1.addGene(new Gene(new int[] {0, 1, 1}, roll1));
        ch1.addGene(new Gene(new int[] {2, 1, 1}, roll2_1));

        Chromosome ch2 = new Chromosome(context);
        ch2.addGene(new Gene(new int[] {2, 1, 1}, roll2_1));
        ch2.addGene(new Gene(new int[] {0, 1, 1}, roll1));

        assertEquals(ch1.hashCode(), ch2.hashCode());
        assertTrue(ch1.equals(ch2));
        assertTrue(ch2.equals(ch1));
    }

    @Test
    public void inactivePattern() {
        Chromosome ch1 = new Chromosome(context);
        ch1.addGene(new Gene(new int[] {0, 1, 1}, roll1));
        ch1.addGene(new Gene(new int[] {2, 1, 1}, roll2_1));
        ch1.addGene(new Gene(new int[] {2, 0, 0}, null));

        Chromosome ch2 = new Chromosome(context);
        ch2.addGene(new Gene(new int[] {2, 1, 1}, roll2_1));
        ch2.addGene(new Gene(new int[] {0, 1, 1}, roll1));

        assertFalse(ch1.hashCode() == ch2.hashCode());
        assertFalse(ch1.equals(ch2));
        assertFalse(ch2.equals(ch1));
    }

    @Test
    public void inactivePatternDifferentOrder() {
        Chromosome ch1 = new Chromosome(context);
        ch1.addGene(new Gene(new int[] {0, 1, 1}, roll1));
        ch1.addGene(new Gene(new int[] {2, 1, 1}, roll2_1));
        ch1.addGene(new Gene(new int[] {2, 0, 0}, null));

        Chromosome ch2 = new Chromosome(context);
        ch2.addGene(new Gene(new int[] {2, 1, 1}, roll2_1));
        ch2.addGene(new Gene(new int[] {2, 0, 0}, null));
        ch2.addGene(new Gene(new int[] {0, 1, 1}, roll1));

        assertEquals(ch1.hashCode(), ch2.hashCode());
        assertTrue(ch1.equals(ch2));
        assertTrue(ch2.equals(ch1));
    }

    @Test
    public void threeGenesRollsFromSameGroup() {
        Chromosome ch1 = new Chromosome(context);
        ch1.addGene(new Gene(new int[] {0, 1, 1}, roll1));
        ch1.addGene(new Gene(new int[] {2, 1, 1}, roll2_1));
        ch1.addGene(new Gene(new int[] {2, 0, 0}, roll2_2));

        Chromosome ch2 = new Chromosome(context);
        ch2.addGene(new Gene(new int[] {2, 1, 1}, roll2_2));
        ch2.addGene(new Gene(new int[] {2, 0, 0}, roll2_1));
        ch2.addGene(new Gene(new int[] {0, 1, 1}, roll1));

        assertEquals(ch1.hashCode(), ch2.hashCode());
        assertTrue(ch1.equals(ch2));
        assertTrue(ch2.equals(ch1));
    }

    @Test
    public void threeGenesDifferentRolls() {
        Chromosome ch1 = new Chromosome(context);
        ch1.addGene(new Gene(new int[] {0, 1, 1}, roll1));
        ch1.addGene(new Gene(new int[] {2, 1, 1}, roll2_1));
        ch1.addGene(new Gene(new int[] {2, 0, 0}, roll2_2));

        Chromosome ch2 = new Chromosome(context);
        ch2.addGene(new Gene(new int[] {2, 1, 1}, roll2_2));
        ch2.addGene(new Gene(new int[] {2, 0, 0}, roll3));
        ch2.addGene(new Gene(new int[] {0, 1, 1}, roll1));

        assertFalse(ch1.hashCode() == ch2.hashCode());
        assertFalse(ch1.equals(ch2));
        assertFalse(ch2.equals(ch1));
    }

    @Test
    public void hashSet() {
        Chromosome ch1 = new Chromosome(context);
        ch1.addGene(new Gene(new int[] {0, 1, 1}, roll1));
        ch1.addGene(new Gene(new int[] {2, 1, 1}, roll2_1));
        ch1.addGene(new Gene(new int[] {2, 0, 0}, roll2_2));

        Chromosome ch2 = new Chromosome(context);
        ch2.addGene(new Gene(new int[] {2, 1, 1}, roll2_2));
        ch2.addGene(new Gene(new int[] {2, 0, 0}, roll2_1));
        ch2.addGene(new Gene(new int[] {0, 1, 1}, roll1));

        Chromosome ch3 = new Chromosome(context);
        ch3.addGene(new Gene(new int[] {2, 1, 1}, roll2_2));
        ch3.addGene(new Gene(new int[] {0, 1, 1}, roll1));

        Set<Chromosome> unique = Sets.newHashSet();
        unique.add(ch1);
        unique.add(ch1); // same instance added twice
        unique.add(ch2); // different instance but same plan
        unique.add(ch3);

        // ch1 and ch2 represent same cutting plan,
        // only 2 unique plans were added to the set
        assertEquals(2, unique.size());
    }
}
