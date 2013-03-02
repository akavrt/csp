package com.akavrt.csp.xml;

import com.akavrt.csp.core.Roll;
import org.jdom2.Element;

import java.text.DecimalFormat;

/**
 * User: akavrt
 * Date: 02.03.13
 * Time: 23:47
 */
public class RollConverter implements XmlConverter<Roll> {

    @Override
    public Element convert(Roll roll) {
        Element rollElm = new Element(RollTags.ROLL);
        rollElm.setAttribute(RollTags.ID, roll.getId());

        Element stripElm = new Element(RollTags.STRIP);
        rollElm.addContent(stripElm);

        DecimalFormat format = new DecimalFormat("#.##");

        // length of the roll
        Element lengthElm = new Element(RollTags.LENGTH);
        lengthElm.setText(format.format(roll.getLength()));
        stripElm.addContent(lengthElm);

        // width width of the roll
        Element widthElm = new Element(RollTags.WIDTH);
        widthElm.setText(format.format(roll.getWidth()));
        stripElm.addContent(widthElm);

        return rollElm;
    }

    public interface RollTags {
        String ROLL = "roll";
        String ID = "id";
        String QUANTITY = "quantity";
        String STRIP = "strip";
        String LENGTH = "length";
        String WIDTH = "width";
    }
}

