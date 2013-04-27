package com.akavrt.csp.tester.params;

import com.akavrt.csp.utils.ParameterSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;

/**
 * User: akavrt
 * Date: 13.04.13
 * Time: 18:57
 */
public abstract class AbstractParametersReader<T extends ParameterSet> implements
        ParameterSetReader<T> {
    private static final Logger LOGGER = LogManager.getLogger(AbstractParametersReader.class);

    protected abstract T createParameterSet();

    @Override
    public T read(File file) {
        T params = null;
        try {
            SAXBuilder sax = new SAXBuilder();
            Document document = sax.build(file);
            if (document.hasRootElement()) {
                Element root = document.getRootElement();

                params = createParameterSet();
                params.loadFromChildElement(root);
            }
        } catch (JDOMException e) {
            LOGGER.catching(e);
        } catch (IOException e) {
            LOGGER.catching(e);
        }

        return params;
    }
}
