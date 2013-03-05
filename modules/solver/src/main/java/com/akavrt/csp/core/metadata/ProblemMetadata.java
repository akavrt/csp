package com.akavrt.csp.core.metadata;

import com.akavrt.csp.xml.MetadataConverter;
import org.jdom2.Element;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * <p>This class can hold any meaningful data about the problem. It is responsible for saving and
 * loading that date to and from XML.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class ProblemMetadata implements MetadataConverter {
    public final static String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm";
    private String name;
    private String author;
    private String description;
    private Date date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element save() {
        Element metadataElm = new Element(ProblemMetadataTags.METADATA);

        Element nameElm = new Element(ProblemMetadataTags.NAME);
        nameElm.setText(getName());
        metadataElm.addContent(nameElm);

        Element authorElm = new Element(ProblemMetadataTags.AUTHOR);
        authorElm.setText(getAuthor());
        metadataElm.addContent(authorElm);

        Element descriptionElm = new Element(ProblemMetadataTags.DESCRIPTION);
        descriptionElm.setText(getDescription());
        metadataElm.addContent(descriptionElm);

        if (getDate() != null) {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.ENGLISH);
            String formatted = df.format(getDate());

            Element dateElm = new Element(ProblemMetadataTags.DATE);
            dateElm.setText(formatted);
            metadataElm.addContent(dateElm);
        }

        return metadataElm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(Element rootElm) {
        Element nameElm = rootElm.getChild(ProblemMetadataTags.NAME);
        if (nameElm != null) {
            setName(nameElm.getText());
        }

        Element authorElm = rootElm.getChild(ProblemMetadataTags.AUTHOR);
        if (authorElm != null) {
            setAuthor(authorElm.getText());
        }

        Element descriptionElm = rootElm.getChild(ProblemMetadataTags.DESCRIPTION);
        if (descriptionElm != null) {
            setDescription(descriptionElm.getText());
        }

        Element dateElm = rootElm.getChild(ProblemMetadataTags.DATE);
        if (dateElm != null) {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.ENGLISH);
            String formatted = dateElm.getText();

            try {
                Date date = df.parse(formatted);
                setDate(date);
            } catch (ParseException e) {
                // TODO add logger statement
                e.printStackTrace();
            }
        }
    }

    public interface ProblemMetadataTags {
        String METADATA = "metadata";
        String NAME = "name";
        String AUTHOR = "author";
        String DATE = "date";
        String DESCRIPTION = "description";
    }

}
