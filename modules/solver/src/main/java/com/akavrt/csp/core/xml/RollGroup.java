package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.Roll;

/**
 * <p>Sometimes when defining a problem it's handy to use brief notation and combine rolls with
 * same size in groups. For example, if we have 5 rolls of the same size, we can enumerate
 * these rolls explicitly using id's like 'roll1', 'roll2', ..., 'roll5'. This is cumbersome
 * and can lead to a lengthy problem definitions.</p>
 *
 * <p>A better approach would be to use something like this: id = 'roll1', quantity = 5, where
 * 'roll1' (let's call it groupId) corresponds not to a single roll but rather to 5 rolls with the
 * same size.</p>
 *
 * <p>In XML:</p>
 *
 * <pre>
 * {@code
 * <rolls>
 *     <!-- group of rolls -->
 *     <roll id="roll1" quantity="5">
 *         <strip>
 *             <length>100</length>
 *             <width>20</width>
 *         </strip>
 *     </roll>
 *
 *     <!-- single roll -->
 *     <roll id="roll2">
 *         <strip>
 *             <length>150</length>
 *             <width>30</width>
 *         </strip>
 *     </roll>
 *     ...
 * </rolls>
 * }
 * </pre>
 *
 * <p>This class is used to model group of rolls in code.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class RollGroup {
    private final Roll roll;
    private int quantity;

    public RollGroup(Roll roll) {
        this.roll = roll;
        quantity = 1;
    }

    public Roll getRoll() {
        return roll;
    }

    public int getQuantity() {
        return quantity;
    }

    public void incQuantity() {
        quantity++;
    }
}
