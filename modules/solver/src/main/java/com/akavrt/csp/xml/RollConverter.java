package com.akavrt.csp.xml;

import com.akavrt.csp.core.Roll;

/**
 * User: akavrt
 * Date: 02.03.13
 * Time: 23:47
 */
public class RollConverter extends StripConverter<Roll> {

    @Override
    public String getRootTag() {
        return RollTags.ROLL;
    }

    @Override
    public Roll createStrip(String id, double length, double width) {
        return new Roll(id, length, width);
    }


    public interface RollTags {
        String ROLL = "roll";
    }
}

