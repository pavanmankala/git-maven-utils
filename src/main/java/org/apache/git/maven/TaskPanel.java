/**
 *
 */
package org.apache.git.maven;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;

import org.apache.git.maven.uiprops.ProcessConfig;

public class TaskPanel extends JPanel {
    private final ProcessConfig config;
    private final JPanel        configPanel;
    private final JTextArea     console;

    public TaskPanel(ProcessConfig config) {
        super(new BorderLayout());
        this.config = config;
        this.console = new JTextArea();
        this.configPanel = new JPanel();


        JPanel consolePanel = new JPanel(new BorderLayout(5, 5));
        consolePanel.add(new JLabel("<html><u><b>Console:</b></u></html>"), BorderLayout.PAGE_START);
        consolePanel.add(new JScrollPane(console));


        JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(configPanel),
                consolePanel);
        pane.setResizeWeight(0.5);
        pane.setOneTouchExpandable(true);
        add(pane);

        initConfigArea();
    }

    private void initConfigArea() {
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));

        JPanel commandPanel = new JPanel();
        commandPanel.setBorder(new TitledBorder("Command Group"));
        commandPanel.setLayout(new BoxLayout(commandPanel, BoxLayout.Y_AXIS));

        JSeparator sep = new JSeparator();
        sep.setMinimumSize(new Dimension(0, 10));
        sep.setMaximumSize(new Dimension(3000, 10));

        commandPanel.add(createTwinGroup(Arrays.asList(new JLabel("Command")),
                Arrays.asList(new JTextField(config.getCommand()))));
        commandPanel.add(Box.createVerticalStrut(5));
        commandPanel.add(sep);
        commandPanel.add(Box.createVerticalStrut(5));
        commandPanel.add(createArgumentPanel());
        commandPanel.add(Box.createVerticalStrut(5));

        JPanel tablePanel = new JPanel();
        TagValueTableModel model = new TagValueTableModel();
        JTable table = new JTable(model);
        commandPanel.add(new JScrollPane(table));
        commandPanel.add(Box.createVerticalStrut(5));

        JPanel envPanel = new JPanel();
        envPanel.setBorder(new TitledBorder("Environment"));
        envPanel.setLayout(new BoxLayout(envPanel, BoxLayout.Y_AXIS));

        TagValueTableModel envmodel = new TagValueTableModel();
        JTable envtable = new JTable(envmodel);
        envPanel.add(new JScrollPane(envtable));
        envPanel.add(Box.createVerticalStrut(5));

        configPanel.add(commandPanel);
        configPanel.add(envPanel);
        configPanel.add(Box.createVerticalGlue());
    }

    private JComponent createArgumentPanel() {
        List<JLabel> labels = new ArrayList<>();
        List<JTextField> argumentsTf = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            labels.add(new JLabel("Argument " + i));
            argumentsTf.add(new JTextField("Argument " + i));
        }

        return createTwinGroup(labels, argumentsTf);
    }

    private JComponent createTwinGroup(List<JLabel> labels, List<JTextField> argumentsTf) {
        JPanel argumentPanel = new JPanel();
        GroupLayout layout = new GroupLayout(argumentPanel);

        argumentPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        ParallelGroup hLabels = layout.createParallelGroup(), hTfs = layout.createParallelGroup();

        for (int i = 0; i < labels.size(); i++) {
            hLabels.addComponent(labels.get(i));
            hTfs.addComponent(argumentsTf.get(i));
        }

        hGroup.addGroup(hLabels);
        hGroup.addGroup(hTfs);

        layout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

        for (int i = 0; i < labels.size(); i++) {
            vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(labels.get(i)).addComponent(argumentsTf.get(i)));
        }

        layout.setVerticalGroup(vGroup);

        return argumentPanel;
    }
}
