package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.MultiCut;
import com.akavrt.csp.core.Roll;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * <p>Represents single patterns applied to different rolls.</p>
 *
 * <p>For more details see PatternGroupConverter.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class PatternGroup {
    private final List<MultiCut> cuts;
    private List<Roll> rolls;

    /**
     * <p>Create pattern initialized with set of cuts and empty list of associated rolls.</p>
     *
     * @param cuts Multipliers stored within the pattern, each multiplier is represented as an
     *             instance of MultiCut.
     */
    public PatternGroup(List<MultiCut> cuts) {
        this.cuts = cuts;
        rolls = Lists.newArrayList();
    }

    /**
     * <p>Set of cuts associated with pattern.</p>
     */
    public List<MultiCut> getCuts() {
        return cuts;
    }

    /**
     * <p>Add roll to the list of rolls associated with pattern.</p>
     */
    public void addRoll(Roll roll) {
        if (roll != null) {
            rolls.add(roll);
        }
    }

    /**
     * <p>List of rolls associated with pattern.</p>
     */
    public List<Roll> getRolls() {
        return rolls;
    }
}
