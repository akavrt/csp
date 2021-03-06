package com.akavrt.csp.solver.evo.operators;

import com.akavrt.csp.core.Roll;
import com.akavrt.csp.solver.evo.Chromosome;
import com.akavrt.csp.solver.evo.EvolutionaryExecutionContext;
import com.akavrt.csp.solver.evo.EvolutionaryOperator;
import com.akavrt.csp.solver.evo.Gene;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * User: akavrt
 * Date: 30.04.13
 * Time: 13:24
 */
public abstract class Mutation implements EvolutionaryOperator {
    protected final Random rGen;

    public Mutation() {
        rGen = new Random();
    }

    @Override
    public void initialize(EvolutionaryExecutionContext context) {
        // nothing to initialize
    }

    protected List<Roll> getSpareRolls(Chromosome chromosome) {
        // mutable list of rolls
        List<Roll> rolls = Lists.newArrayList(chromosome.getContext().getProblem().getRolls());

        Set<Integer> usedRollIds = Sets.newHashSet();
        for (Gene gene : chromosome.getGenes()) {
            if (gene.getRoll() != null) {
                usedRollIds.add(gene.getRoll().getInternalId());
            }
        }

        int i = 0;
        while (i < rolls.size()) {
            int id = rolls.get(i).getInternalId();
            if (usedRollIds.contains(id)) {
                rolls.remove(i);
            } else {
                i++;
            }
        }

        return rolls;
    }

    protected Roll pickRoll(Chromosome chromosome) {
        List<Roll> rolls = getSpareRolls(chromosome);

        Roll picked = null;
        if (rolls.size() > 0) {
            int rollIndex = rGen.nextInt(rolls.size());
            picked = rolls.get(rollIndex);
        }

        return picked;
    }

    protected Roll pickRoll(double patternWidth, Chromosome chromosome) {
        List<Roll> rolls = getSpareRolls(chromosome);

        // TODO here we can control trim and select roll with minimal trim
        Roll picked = null;
        for (Roll roll : rolls) {
            if (roll.getWidth() >= patternWidth) {
                picked = roll;
                break;
            }
        }

        return picked;
    }

    protected List<GeneGroup> groupGenes(Chromosome chromosome) {
        Map<Integer, GeneGroup> groups = Maps.newHashMap();
        for (int i = 0; i < chromosome.size(); i++) {
            Gene gene = chromosome.getGene(i);
            int patternHash = gene.getPatternHashCode();

            GeneGroup group = groups.get(patternHash);
            if (group == null) {
                group = new GeneGroup();
                groups.put(patternHash, group);
            }

            group.addGene(i, gene);
        }

        return Lists.newArrayList(groups.values());
    }

    protected double getWidthToleranceRatio(Chromosome chromosome) {
        double trimRatio = chromosome.getMetricProvider().getTrimRatio();
        return Math.min(1.1 * trimRatio, 1);
    }

}
