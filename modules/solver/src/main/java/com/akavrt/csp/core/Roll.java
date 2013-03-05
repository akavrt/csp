package com.akavrt.csp.core;

import com.akavrt.csp.core.metadata.RollMetadata;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * <p>This class represents a single unit of stock material.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class Roll extends Strip {
    private static final String FORMAT_TEMPLATE = "Roll '%s':\n    W = %.2f\n    L = %.2f";
    private int internalId;
    private RollMetadata metadata;

    /**
     * <p>Create roll with predefined identifier, length and width.</p>
     *
     * @param id     Unique identifier of the roll.
     * @param length Length of the roll, measured in abstract units.
     * @param width  Width of the roll, measured in abstract units.
     */
    public Roll(String id, double length, double width) {
        this(id, 1, length, width);
    }

    /**
     * <p>Create roll with predefined identifier, length and width.</p>
     *
     * <p>Sometimes when defining a problem it's handy to use brief notation and combine rolls with
     * same size in groups. For example, if we have 5 rolls of the same size, we can enumerate
     * these rolls explicitly using id's like "roll1", "roll2", ..., "roll5". This is cumbersome
     * and can lead to a lengthy problem definitions.</p>
     *
     * <p>A better approach would be something like this: id = "roll1", quantity = 5, where "roll1"
     * (let's call it groupId) corresponds not to a single roll but rather to 5 rolls with the same
     * size. To differentiate these rolls we should introduce new additional identifier (let's call
     * it rollId) applied to a lower level (uniqueness have to be insured only within group of
     * rolls). Having groupId and rollId combined in any suitable way we can obtain internal
     * identifier which will be unique across all of the rolls defined within single problem.</p>
     *
     * @param groupId Unique String identifier of the group of rolls.
     * @param rollId  Unique numeric identifier of the roll within group of rolls.
     * @param length  Length of the roll, measured in abstract units.
     * @param width   Width of the roll, measured in abstract units.
     */
    public Roll(String groupId, int rollId, double length, double width) {
        super(groupId, length, width);

        internalId = calculateInternalId(rollId);
    }

    private int calculateInternalId(int rollId) {
        return new HashCodeBuilder().append(getId()).append(rollId).toHashCode();
    }

    /**
     * <p>Unique internal identifier is used for validity checks and inside the optimization
     * routine. Calculated based on the id of the roll.</p>
     *
     * @return The unique internal identifier of the roll.
     */
    public int getInternalId() {
        return internalId;
    }

    /**
     * <p>Area of the roll. Measured in abstract square units.</p>
     *
     * @return The usable area of the roll.
     */
    public double getArea() {
        return getLength() * getWidth();
    }

    /**
     * {@inheritDoc}
     *
     * @return String representation of the roll with custom formatting.
     */
    @Override
    public String toString() {
        return String.format(FORMAT_TEMPLATE, getId(), getWidth(), getLength());
    }

    /**
     * <p>Provide extended description of the roll.</p>
     *
     * @return Additional information about the roll.
     */
    public RollMetadata getMetadata() {
        return metadata;
    }

    /**
     * <p>Set additional information about the roll.</p>
     *
     * @param metadata Additional information about the roll.
     */
    public void setMetadata(RollMetadata metadata) {
        this.metadata = metadata;
    }
}
