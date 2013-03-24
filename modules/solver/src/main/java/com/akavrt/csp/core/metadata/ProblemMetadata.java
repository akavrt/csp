package com.akavrt.csp.core.metadata;

import com.akavrt.csp.xml.XmlUtils;
import com.akavrt.csp.utils.Unit;
import com.akavrt.csp.utils.Utils;
import com.akavrt.csp.xml.XmlCompatible;
import org.jdom2.Element;

import java.util.Date;

/**
 * <p>This class can hold any meaningful data about the problem. It is responsible for saving and
 * loading that data to and from XML.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class ProblemMetadata implements XmlCompatible {
    private String name;
    private String author;
    private String description;
    private Date date;
    private Unit units;

    /**
     * <p>Precise self-descriptive name used to identify problem.</p>
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Set precise self-descriptive name used to identify problem.</p>
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Information about the author of the problem.</p>
     */
    public String getAuthor() {
        return author;
    }

    /**
     * <p>Set information about the author of the problem.</p>
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * <p>Textual description of the problem. Characteristic specific to the problem should be
     * extensively described using this field.</p>
     */
    public String getDescription() {
        return description;
    }

    /**
     * <p>Set textual description of the problem.</p>
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * <p>Indicates date when problem was created.</p>
     */
    public Date getDate() {
        return date;
    }

    /**
     * <p>Set date indicating the moment when problem was created.</p>
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * <p>Units of measure used in problem definition. It's expected that same units used to
     * describe linear dimensions of all orders and rolls involved.</p>
     */
    public Unit getUnits() {
        return units;
    }

    /**
     * <p>Set units of measure used in problem definition.</p>
     */
    public void setUnits(Unit units) {
        this.units = units;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element save() {
        Element metadataElm = new Element(XmlTags.METADATA);

        if (!Utils.isEmpty(getName())) {
            Element nameElm = new Element(XmlTags.NAME);
            nameElm.setText(getName());
            metadataElm.addContent(nameElm);
        }

        if (!Utils.isEmpty(getAuthor())) {
            Element authorElm = new Element(XmlTags.AUTHOR);
            authorElm.setText(getAuthor());
            metadataElm.addContent(authorElm);
        }

        if (!Utils.isEmpty(getDescription())) {
            Element descriptionElm = new Element(XmlTags.DESCRIPTION);
            descriptionElm.setText(getDescription());
            metadataElm.addContent(descriptionElm);
        }

        if (getDate() != null) {
            Element dateElm = new Element(XmlTags.DATE);
            dateElm.setText(XmlUtils.formatDate(getDate()));
            metadataElm.addContent(dateElm);
        }

        if (getUnits() != null) {
            Element unitsElm = new Element(XmlTags.UNITS);
            unitsElm.setText(getUnits().getSymbol());
            metadataElm.addContent(unitsElm);
        }

        return metadataElm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(Element rootElm) {
        Element nameElm = rootElm.getChild(XmlTags.NAME);
        if (nameElm != null) {
            setName(nameElm.getText());
        }

        Element authorElm = rootElm.getChild(XmlTags.AUTHOR);
        if (authorElm != null) {
            setAuthor(authorElm.getText());
        }

        Element descriptionElm = rootElm.getChild(XmlTags.DESCRIPTION);
        if (descriptionElm != null) {
            setDescription(descriptionElm.getText());
        }

        Element dateElm = rootElm.getChild(XmlTags.DATE);
        if (dateElm != null) {
            setDate(XmlUtils.getDateFromText(dateElm));
        }

        Element unitsElm = rootElm.getChild(XmlTags.UNITS);
        if (unitsElm != null && unitsElm.getText() != null) {
            String value = unitsElm.getText();
            Unit units = null;
            for (Unit unit : Unit.values()) {
                if (unit.getName().equalsIgnoreCase(value)
                        || unit.getSymbol().equalsIgnoreCase(value)) {
                    units = unit;
                    break;
                }
            }

            setUnits(units);
        }
    }

    private interface XmlTags {
        String METADATA = "metadata";
        String NAME = "name";
        String AUTHOR = "author";
        String DATE = "date";
        String DESCRIPTION = "description";
        String UNITS = "units";
    }

}
