package com.akavrt.csp.genetic;

import com.akavrt.csp.solver.genetic.Chromosome;
import com.akavrt.csp.solver.genetic.Gene;
import com.akavrt.csp.solver.genetic.GeneticBinaryOperator;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * User: akavrt
 * Date: 29.03.13
 * Time: 02:10
 */
public class Crossover implements GeneticBinaryOperator {
    private static final Logger LOGGER = LogManager.getLogger(Crossover.class);
    private final Random rGen;

    public Crossover() {
        rGen = new Random();
    }

    @Override
    public Chromosome apply(Chromosome firstParent, Chromosome secondParent) {
        if (firstParent == null || secondParent == null) {
            return null;
        }

        Chromosome child = new Chromosome(firstParent.getContext());

        List<Gene> pool = Lists.newArrayList();
        for (Gene gene : firstParent.getGenes()) {
            pool.add(gene);
        }

        for (Gene gene : secondParent.getGenes()) {
            pool.add(gene);
        }

        double previousRatio;
        double currentRatio;
        Set<Integer> rollIds = Sets.newHashSet();
        do {
            previousRatio = getRatio(child);

            // randomly pick one of the genes in the pool
            int index = rGen.nextInt(pool.size());
            Gene gene = pool.remove(index);
            if (gene.getRoll() == null || rollIds.add(gene.getRoll().getInternalId())) {
                // create a copy of the gene and add it to the child
                child.addGene(new Gene(gene));
                currentRatio = getRatio(child);
            } else {
                // child left unchanged
                currentRatio = previousRatio;
            }
        }
        while (currentRatio <= previousRatio && pool.size() > 0);

        if (currentRatio > previousRatio && child.size() > 1) {
            // TODO in future we should apply a simple heuristic rule to refine child here
            // remove last gene
            child.removeGene(child.size() - 1);
        }

        LOGGER.debug("CX: ch1({} genes), ch2({} genes) -> child({} genes)", firstParent.size(),
                     secondParent.size(), child.size());

        return child;
    }

    private double getRatio(Chromosome chromosome) {
        return chromosome.getMetricProvider().getAverageUnderProductionRatio() +
                chromosome.getMetricProvider().getAverageOverProductionRatio();
    }

}
