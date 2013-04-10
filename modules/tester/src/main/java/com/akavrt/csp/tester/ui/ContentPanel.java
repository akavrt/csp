package com.akavrt.csp.tester.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

/**
 * User: akavrt
 * Date: 09.04.13
 * Time: 15:01
 */
public class ContentPanel extends JTabbedPane {
    private final TextTracePanel textTracePanel;
    private final XmlPanel xmlPanel;

    public ContentPanel() {
        textTracePanel = new TextTracePanel();
        xmlPanel = new XmlPanel();

        addTab("text trace", textTracePanel);
        addTab("graph trace", new GraphTracePanel());
        addTab("analyze", new AnalyzerPanel());
        addTab("XML", xmlPanel);

        setTabPlacement(JTabbedPane.BOTTOM);
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        setUI(new BasicTabbedPaneUI() {
            @Override
            protected Insets getContentBorderInsets(int tabPlacement) {
                return new Insets(0, 0, 0, 0);
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            }

            @Override
            protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects,
                                               int tabIndex, Rectangle iconRect,
                                               Rectangle textRect, boolean isSelected) {
            }
        });

        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
    }

    public void clearTextArea() {
        textTracePanel.clearTextArea();
    }

    public void appendText(String text) {
        textTracePanel.appendText(text);
    }

    public void setXml(String formattedXml) {
        xmlPanel.setXml(formattedXml);
    }

}
