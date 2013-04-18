package com.akavrt.csp.genetic;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.Roll;
import com.akavrt.csp.solver.genetic.Chromosome;
import com.akavrt.csp.solver.genetic.Gene;
import com.akavrt.csp.solver.genetic.GeneticExecutionContext;
import com.akavrt.csp.solver.genetic.GeneticOperator;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * User: akavrt
 * Date: 16.04.13
 * Time: 19:10
 */
public abstract class PatternBasedMutation implements GeneticOperator {
    protected final PatternGenerator generator;
    protected final Random rGen;

    public PatternBasedMutation(PatternGenerator generator) {
        this.generator = generator;
        rGen = new Random();
    }

    @Override
    public void initialize(GeneticExecutionContext context) {
        generator.initialize(context.getProblem());
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

    protected int[] calcDemand(double stockLength, Chromosome prototype) {
        List<Order> orders = prototype.getContext().getProblem().getOrders();

        int[] demand = new int[orders.size()];
        for (int i = 0; i < orders.size(); i++) {
            double produced = prototype.getProductionLengthForOrder(i);
            double orderLength = orders.get(i).getLength();

            if (orderLength > produced) {
                double ratio = (orderLength - produced) / stockLength;
                demand[i] = ratio < 1 ? 1 : (int) Math.floor(ratio);
            }
        }

        return demand;
    }

    protected Roll pickRoll(double patternWidth, Chromosome chromosome) {
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

}
