package com.akavrt.csp.genetic;

import com.akavrt.csp.core.Roll;
import com.akavrt.csp.solver.genetic.Gene;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * User: akavrt
 * Date: 16.04.13
 * Time: 21:14
 */
public class GeneGroup {
    private List<Integer> indices;
    private List<Gene> genes;

    public GeneGroup() {
        indices = Lists.newArrayList();
        genes = Lists.newArrayList();
    }

    public int size() {
        return genes.size();
    }

    public Gene getGene(int index) {
        return genes.get(index);
    }

    public void setGene(int index, Gene gene) {
        genes.set(index, gene);
    }

    public void removeGene(int index, boolean adjustIndices) {
        int removedIndex = indices.remove(index);
        if (adjustIndices) {
            for (int i = 0; i < indices.size(); i++) {
                if (indices.get(i) > removedIndex) {
                    indices.set(i, indices.get(i) - 1);
                }
            }
        }

        genes.remove(index);
    }

    public int getGeneIndex(int index) {
        return indices.get(index);
    }

    public void addGene(int index, Gene gene) {
        indices.add(index);
        genes.add(gene);
    }

    public double getTotalLength() {
        double length = 0;
        for (Gene gene : genes) {
            length += gene.getRoll() == null ? 0 : gene.getRoll().getLength();
        }

        return length;
    }

    public double getMinWidth() {
        double minWidth = 0;
        boolean unset = true;
        for (Gene gene : genes) {
            Roll roll = gene.getRoll();
            if (roll != null && (unset || roll.getWidth() < minWidth)) {
                minWidth = roll.getWidth();
                unset = false;
            }
        }

        return minWidth;
    }

}
