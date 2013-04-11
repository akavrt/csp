package com.akavrt.csp.tester.ui.content;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.tester.ui.SeriesData;
import com.akavrt.csp.tester.ui.SeriesMetricProvider;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.util.List;

/**
 * User: akavrt
 * Date: 09.04.13
 * Time: 15:01
 */
public class ContentPanel extends JTabbedPane {
    private final TextTracePanel textTracePanel;
    private final GraphTracePanel graphTracePanel;
    private final AnalyzerPanel analyzerPanel;
    private final XmlPanel xmlPanel;

    public ContentPanel(OnSolutionsSelectedListener listener) {
        textTracePanel = new TextTracePanel();
        graphTracePanel = new GraphTracePanel();
        analyzerPanel = new AnalyzerPanel(listener);
        xmlPanel = new XmlPanel();

        addTab("text trace", textTracePanel);
        addTab("graph trace",graphTracePanel);
        addTab("analyze", analyzerPanel);
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

    public void setFocus() {
        textTracePanel.setFocus();
    }

    public void clearSeries() {
        graphTracePanel.clearSeries();
    }

    public void updateSeries(SeriesData data) {
        graphTracePanel.updateSeries(data);
    }

    public void setAnalyzerData(Problem problem, List<Solution> solutions,
                                SeriesMetricProvider metricProvider) {
        analyzerPanel.setData(problem, solutions, metricProvider);
    }

    public void clearAnalyzerData() {
        analyzerPanel.clearData();
    }

    public interface OnSolutionsSelectedListener {
        void onSolutionsSelected(List<Solution> selection);
    }

}
