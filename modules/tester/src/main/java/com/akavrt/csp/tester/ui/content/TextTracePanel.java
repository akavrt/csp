package com.akavrt.csp.tester.ui.content;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Date;

/**
 * User: akavrt
 * Date: 09.04.13
 * Time: 19:11
 */
public class TextTracePanel extends JPanel {
    private final JTextArea textArea;

    public TextTracePanel() {
        textArea = new JTextArea();
        textArea.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        textArea.append("<!-- Trace start at " + new Date() + " -->\n");
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        setBackground(Color.white);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    public void clearTextArea() {
        textArea.setText("");
        textArea.append("<!-- Trace start at " + new Date() + " -->\n");
    }

    public void appendText(String text) {
        textArea.append("\n");
        textArea.append(text);
    }

    public void setFocus() {
        textArea.requestFocusInWindow();
    }
}
