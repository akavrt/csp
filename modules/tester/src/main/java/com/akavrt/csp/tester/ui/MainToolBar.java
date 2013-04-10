package com.akavrt.csp.tester.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * User: akavrt
 * Date: 09.04.13
 * Time: 14:46
 */
public class MainToolBar extends JToolBar {
    private final OnActionPerformedListener listener;
    private JProgressBar progressBar;
    private AbstractAction loadAction;
    private AbstractAction saveAction;
    private AbstractAction clearAction;
    private AbstractAction startAction;
    private AbstractAction stopAction;

    public MainToolBar(OnActionPerformedListener listener) {
        this.listener = listener;
        setFloatable(false);
        setRollover(true);

        setupActions();
        setupContent();

        setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
    }

    private void setupActions() {
        loadAction = new BaseAction("Load", "Load problem from file", new Integer(KeyEvent.VK_O),
                                    KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK)) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (listener != null) {
                    listener.loadProblem();
                }
            }
        };

        saveAction = new BaseAction("Save", "Save data to file", new Integer(KeyEvent.VK_S),
                                    KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK)) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (listener != null) {
                    listener.saveData();
                }
            }
        };

        clearAction = new BaseAction("Clear", "Stop calculations", new Integer(KeyEvent.VK_L),
                                     KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK)) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (listener != null) {
                    listener.clearTrace();
                }
            }
        };

        startAction = new BaseAction("Start", "Start calculations", new Integer(KeyEvent.VK_R),
                                     KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK)) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (listener != null && listener.startCalculations()) {
                    startAction.setEnabled(false);
                    stopAction.setEnabled(true);
                    progressBar.setVisible(true);
                }
            }
        };

        stopAction = new BaseAction("Stop", "Stop calculations", new Integer(KeyEvent.VK_T),
                                    KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK)) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                startAction.setEnabled(true);
                stopAction.setEnabled(false);
                progressBar.setVisible(false);

                if (listener != null) {
                    listener.stopCalculations();
                }
            }
        };
        stopAction.setEnabled(false);
    }

    private void setupContent() {
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);

        JButton loadButton = new JButton(loadAction);
        loadButton.setMargin(new Insets(5, 5, 5, 5));
        loadButton.setFocusable(false);

        JButton saveButton = new JButton(saveAction);
        saveButton.setMargin(new Insets(5, 5, 5, 5));
        saveButton.setFocusable(false);

        JButton clearButton = new JButton(clearAction);
        clearButton.setMargin(new Insets(5, 5, 5, 5));
        clearButton.setFocusable(false);

        JButton startButton = new JButton(startAction);
        startButton.setMargin(new Insets(5, 5, 5, 5));
        startButton.setFocusable(false);

        JButton stopButton = new JButton(stopAction);
        stopButton.setMargin(new Insets(5, 5, 5, 5));
        stopButton.setFocusable(false);

        add(loadButton);
        add(saveButton);
        addSeparator();
        add(clearButton);
        addSeparator();
        add(startButton);
        add(stopButton);
        addSeparator();

        add(progressBar);
    }

    public interface OnActionPerformedListener {
        void loadProblem();

        void saveData();

        void clearTrace();

        boolean startCalculations();

        void stopCalculations();
    }

    private static abstract class BaseAction extends AbstractAction {
        public BaseAction(String text, String desc, Integer mnemonic, KeyStroke accelerator) {
            super(text);

            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
            putValue(ACCELERATOR_KEY, accelerator);
        }
    }
}
