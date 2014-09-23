/**
 *
 */
package org.apache.git.maven;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.git.maven.task.GitMavenAction;
import org.apache.git.maven.task.gittask.GitActionUtils;
import org.apache.git.maven.uiprops.ProcessConfig;

public class TaskPanel extends JPanel {
    private static final ServiceLoader<GitMavenAction> ACTIONS       = ServiceLoader
                                                                             .load(GitMavenAction.class);
    private final ProcessConfig                        config;
    private final JPanel                               configPanel;
    private final JTextArea                            console;
    private final Action                               executeAction = new AbstractAction() {
                                                                         {
                                                                             putValue(Action.NAME,
                                                                                     "Execute");
                                                                         }

                                                                         @Override
                                                                         public void actionPerformed(
                                                                                 ActionEvent e) {
                                                                             execute();
                                                                         }
                                                                     };

    public TaskPanel(ProcessConfig config) {
        super(new BorderLayout());
        this.config = config;
        this.console = new JTextArea();
        this.configPanel = new JPanel();


        JPanel consolePanel = new JPanel(new BorderLayout(3, 3));
        consolePanel.add(new JLabel("<html><u><b>Console:</b></u></html>"), BorderLayout.PAGE_START);
        consolePanel.add(new JScrollPane(console));
        console.setEditable(false);
        console.setFont(new Font("Courier New", Font.PLAIN, 12));

        add(new JScrollPane(configPanel), BorderLayout.PAGE_START);
        add(consolePanel, BorderLayout.CENTER);
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

        commandPanel.add(createCommandPanel());
        commandPanel.add(Box.createVerticalStrut(5));

        JComponent argVis = createArgumentPanel();

        if (argVis != null) {
            commandPanel.add(sep);
            commandPanel.add(Box.createVerticalStrut(5));
            commandPanel.add(createArgumentPanel());
        }

        commandPanel.add(Box.createVerticalStrut(5));
        commandPanel.add(createCollapsibleTablePanel());

        commandPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        configPanel.add(commandPanel);
        configPanel.add(Box.createVerticalStrut(5));
        configPanel.add(createExecutePanel());
        configPanel.add(Box.createVerticalStrut(5));
        configPanel.add(new JSeparator() {
            {
                setAlignmentX(Component.LEFT_ALIGNMENT);
            }
        });
    }

    private JComponent createCommandPanel() {
        JTextField command = new MyTextField("titleId", config.getTitleId());
        command.setEnabled(false);

        JTextField baseDir = new MyTextField("baseDir", config.getBaseDir().getAbsolutePath());

        return createTwinGroup(Arrays.asList(new JLabel("Command"), new JLabel("baseDir")),
                Arrays.asList(command, baseDir));
    }

    private JComponent createExecutePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(new JButton(executeAction));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    JComponent createCollapsibleTablePanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        final JComponent tablePanel = createTwinGroup(
                Arrays.asList(createTagValueArgumentPanel("Additional Arguments")),
                Arrays.asList(createTagValueArgumentPanel("Environment Variables")));
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(new JCheckBox(new AbstractAction() {
            {
                putValue(Action.NAME, "Show More");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox box = ((JCheckBox) e.getSource());
                tablePanel.setVisible(box.isSelected());
                if (box.isSelected()) {
                    panel.add(tablePanel);
                } else {
                    panel.remove(tablePanel);
                }
                TaskPanel.this.revalidate();
            }
        }) {
            {
                setSelected(false);
                setAlignmentX(Component.LEFT_ALIGNMENT);
            }
        });

        panel.add(tablePanel);

        tablePanel.setVisible(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        return panel;
    }

    JComponent createTagValueArgumentPanel(String title) {
        TagValueTableModel model = new TagValueTableModel();
        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane pane = new JScrollPane(new JTable(model));
        panel.add(pane);
        panel.setBorder(BorderFactory.createTitledBorder(title));
        pane.setPreferredSize(new Dimension(0, 150));
        pane.setMaximumSize(new Dimension(3000, 150));
        return panel;
    }

    private JComponent createArgumentPanel() {
        if (config.getArguments() == null) {
            return null;
        }

        List<JLabel> labels = new ArrayList<>();
        List<JTextField> argumentsTf = new ArrayList<>();

        for (Entry<String, String> e : config.getArguments().entrySet()) {
            labels.add(new JLabel(e.getKey()));
            argumentsTf.add(new MyTextField(e.getKey(), e.getValue()));
        }

        return createTwinGroup(labels, argumentsTf);
    }

    private JComponent createTwinGroup(Collection<? extends JComponent> item1,
            Collection<? extends JComponent> item2) {
        int size = item1.size();
        if (size != item2.size()) {
            throw new RuntimeException("no of item1 != no of item2");
        }

        JPanel groupedPanel = new JPanel();
        GroupLayout layout = new GroupLayout(groupedPanel);

        groupedPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        ParallelGroup hItem1Group = layout.createParallelGroup(), hItem2Group = layout
                .createParallelGroup();

        Iterator<? extends JComponent> item1Iter = item1.iterator();
        Iterator<? extends JComponent> item2Iter = item2.iterator();

        while (item1Iter.hasNext() && item2Iter.hasNext()) {
            hItem1Group.addComponent(item1Iter.next());
            hItem2Group.addComponent(item2Iter.next());
        }

        hGroup.addGroup(hItem1Group);
        hGroup.addGroup(hItem2Group);

        layout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

        item1Iter = item1.iterator();
        item2Iter = item2.iterator();

        while (item1Iter.hasNext() && item2Iter.hasNext()) {
            vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(item1Iter.next()).addComponent(item2Iter.next()));
        }

        layout.setVerticalGroup(vGroup);
        groupedPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return groupedPanel;
    }

    private void execute() {
        config.save();

        final Map<String, GitMavenAction> actionSq = new LinkedHashMap<>();
        for (String actionName : config.getActionSequence()) {
            actionSq.put(actionName, null);
        }

        for (GitMavenAction action : ACTIONS) {
            if (actionSq.containsKey(action.getActionName())) {
                actionSq.put(action.getActionName(), action);
            }
        }

        ExecutionService.execute(new Runnable() {
            @Override
            public void run() {
                PrintWriter log = new PrintWriter(new OutputStream() {
                    @Override
                    public void write(int b) throws IOException {
                        writeToConsole(String.valueOf((char) b));
                    }

                    @Override
                    public void write(byte[] b) throws IOException {
                        writeToConsole(new String(b));
                    }

                    void writeToConsole(String s) {
                        console.append(s);
                        console.setCaretPosition(console.getDocument().getLength());
                    }

                    @Override
                    public void write(byte[] b, int off, int len) throws IOException {
                        writeToConsole(new String(b, off, len));
                    }
                }, true);

                GitActionUtils utils;
                try {
                    utils = new GitActionUtils(config.getBaseDir().getAbsolutePath());
                } catch (IOException e1) {
                    log.println("Unable to create Git repo @ "
                            + config.getBaseDir().getAbsolutePath());
                    return;
                }
                try {
                    log.println("########### PROCESS BEGIN ###########");
                    for (Entry<String, GitMavenAction> e : actionSq.entrySet()) {
                        log.println("--------------- ACTION BEGIN : " + e.getKey()
                                + "------------------");
                        try {
                            e.getValue().execute(utils, config, log);
                        } catch (Throwable e1) {
                            e1.printStackTrace(log);
                            break;
                        }

                        log.println("--------------- ACTION END : " + e.getKey()
                                + "------------------");
                    }
                    log.println("########### PROCESS ENDS ###########");
                } finally {
                    utils.utilCloseRepo();
                }
            }
        });
    }

    class MyTextField extends JTextField {
        public MyTextField(final String argument, String initialText) {
            super(initialText);
            getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void removeUpdate(DocumentEvent e) {
                    update();
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    update();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    update();
                }

                private void update() {
                    switch (argument) {
                        case "baseDir":
                            File f = new File(getText());
                            if (f.exists() && f.isDirectory()) {
                                config.setBaseDir(f);
                                executeAction.setEnabled(true);
                            } else {
                                executeAction.setEnabled(false);
                            }
                            break;
                        default:
                            config.getArguments().put(argument, getText());
                            break;
                    }
                }
            });
        }
    }
}
