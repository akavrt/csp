package com.akavrt.csp.core.metadata;

import com.akavrt.csp.core.xml.XmlUtils;
import com.akavrt.csp.utils.Utils;
import com.akavrt.csp.xml.XmlCompatible;
import com.google.common.collect.Lists;
import org.jdom2.Element;

import java.util.Date;
import java.util.List;

/**
 * <p>This class can hold any meaningful data about the solution. It is responsible for saving and
 * loading that data to and from XML.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class SolutionMetadata implements XmlCompatible {
    private final List<XmlCompatible> parameters;
    private String description;
    private Date date;

    public SolutionMetadata() {
        parameters = Lists.newArrayList();
    }

    /**
     * <p>Return textual description of the solution.</p>
     */
    public String getDescription() {
        return description;
    }

    /**
     * <p>Set textual description of the solution in hand. Can be used to specify method name which
     * was used to obtain solution or to describe specific properties of the solution.</p>
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * <p>Return date and time when solution was obtained.</p>
     */
    public Date getDate() {
        return date;
    }

    /**
     * <p>Set date and time when solution was obtained.</p>
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * <p>Sometimes it's useful to save which method and with what set of parameters was used to
     * obtain this particular solution. To make life more easier all important parameter classes
     * implements XmlCompatible interface. Thus, parameters can be exported to XML. Solution can be
     * accompanied by any number of parameter sets, all of them will be added to XML during
     * export.</p>
     *
     * <p>Reverse conversion, i.e. extraction of the parameter values linked with solution in XML,
     * currently is not supported.</p>
     *
     * @param parameters Set of parameters to be stored with solution in XML.
     */
    public void addParameters(XmlCompatible parameters) {
        this.parameters.add(parameters);
    }

    /**
     * <p>Remove any parameter sets previously added to the solution.</p>
     */
    public void clearParameters() {
        this.parameters.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element save() {
        Element metadataElm = new Element(XmlTags.METADATA);

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

        if (parameters.size() > 0) {
            Element paramsElm = new Element(XmlTags.PARAMETERS);
            metadataElm.addContent(paramsElm);

            for (XmlCompatible params : parameters) {
                paramsElm.addContent(params.save());
            }
        }

        return metadataElm;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Extraction of the parameter sets stored within metadata doesn't supported in current
     * version. It means that if you extract solution from XML and than save it back, parameter
     * sets
     * data will be lost.</p>
     */
    @Override
    public void load(Element rootElm) {
        Element descriptionElm = rootElm.getChild(XmlTags.DESCRIPTION);
        if (descriptionElm != null) {
            setDescription(descriptionElm.getText());
        }

        Element dateElm = rootElm.getChild(XmlTags.DATE);
        if (dateElm != null) {
            setDate(XmlUtils.getDateFromText(dateElm));
        }
    }

    private interface XmlTags {
        String METADATA = "metadata";
        String DESCRIPTION = "description";
        String DATE = "date";
        String PARAMETERS = "parameters";
    }

}
