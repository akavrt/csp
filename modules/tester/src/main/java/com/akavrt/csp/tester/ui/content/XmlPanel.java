package com.akavrt.csp.tester.ui.content;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;

/**
 * User: akavrt
 * Date: 09.04.13
 * Time: 19:13
 */
public class XmlPanel extends JPanel {
    private final JTextArea xmlArea;

    public XmlPanel() {
        xmlArea = new JTextArea();
        xmlArea.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
        xmlArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        xmlArea.setEditable(false);

        DefaultCaret caret = (DefaultCaret) xmlArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

        JScrollPane scrollPane = new JScrollPane(xmlArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        setBackground(Color.white);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setXml(String formattedXml) {
        xmlArea.setText(formattedXml);
    }

}
