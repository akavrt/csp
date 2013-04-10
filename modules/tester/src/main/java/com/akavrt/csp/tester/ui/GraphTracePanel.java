package com.akavrt.csp.tester.ui;

import javax.swing.*;
import java.awt.*;

/**
 * User: akavrt
 * Date: 09.04.13
 * Time: 19:12
 */
public class GraphTracePanel extends JPanel {

    public GraphTracePanel() {
        JLabel filler = new JLabel("graph trace");
        filler.setHorizontalAlignment(JLabel.CENTER);
        setLayout(new GridLayout(1, 1));
        add(filler);

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
    }

}
