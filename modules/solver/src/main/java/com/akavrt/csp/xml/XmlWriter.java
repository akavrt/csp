package com.akavrt.csp.xml;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>Utility class used to convert data to XML and write result into stream or file.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public abstract class XmlWriter {

    /**
     * <p>Convert data into XML represented as an instance of org.jdom2.Document.</p>
     *
     * @return Data structure with data converted to XML.
     */
    public abstract Element convert();

    /**
     * <p>Convert data to XML and write it into stream.</p>
     *
     * @param out          OutputStream to use.
     * @param prettyFormat Use whitespace beautification.
     * @throws java.io.IOException If any IO-related problem occurs while writing.
     */
    public void write(OutputStream out, boolean prettyFormat) throws IOException {
        Element rootElm = convert();
        Document doc = new Document(rootElm);

        XMLOutputter outputter;
        if (prettyFormat) {
            outputter = new XMLOutputter(Format.getPrettyFormat());
        } else {
            outputter = new XMLOutputter();
        }

        outputter.output(doc, out);
    }

    /**
     * <p>Convert data to XML and write it into file. Any previous data stored in this file will be
     * lost.</p>
     *
     * @param file         File to use.
     * @param prettyFormat Use whitespace beautification.
     * @throws IOException If any IO-related problem occurs while writing.
     */
    public void write(File file, boolean prettyFormat) throws IOException {
        Element rootElm = convert();
        Document doc = new Document(rootElm);

        XMLOutputter outputter;
        if (prettyFormat) {
            outputter = new XMLOutputter(Format.getPrettyFormat());
        } else {
            outputter = new XMLOutputter();
        }

        FileWriter writer = new FileWriter(file, false);
        outputter.output(doc, writer);
    }

}
