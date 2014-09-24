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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
import org.apache.git.maven.uiprops.ProcessConfig.ActionConfig;

public class TaskPanel extends JPanel {
    private static final Map<String, GitMavenAction> ACTION_MAP;
    private static final Icon                        ADD, DELETE;
    private final ProcessConfig                      config;
    private final JPanel                             configPanel;
    private final JTextArea                          console;
    private final DisabledPanel                      disabledPanel;
    private final Action                             executeAction = new AbstractAction() {
                                                                       @Override
                                                                       public void actionPerformed(
                                                                               ActionEvent e) {
                                                                           execute();
                                                                       }
                                                                   };

    static {
        Map<String, GitMavenAction> actionMap = new HashMap<>();
        for (GitMavenAction action : ServiceLoader.load(GitMavenAction.class)) {
            actionMap.put(action.getActionName(), action);
        }

        ACTION_MAP = Collections.unmodifiableMap(actionMap);
        try {
            ADD = new ImageIcon(ImageIO.read(TaskPanel.class.getResourceAsStream("/images/add.png")));
            DELETE = new ImageIcon(ImageIO.read(TaskPanel.class
                    .getResourceAsStream("/images/delete.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TaskPanel(ProcessConfig config) {
        super(new BorderLayout());
        executeAction.putValue(Action.NAME, "Execute");
        this.config = config;
        this.console = new JTextArea();
        this.configPanel = new JPanel();


        console.setEditable(false);
        console.setFont(new Font("Courier New", Font.PLAIN, 12));
        final JPopupMenu popup = new JPopupMenu();
        Action clearConsole = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                console.setText("");
            }
        };
        clearConsole.putValue(Action.NAME, "Clear Console");
        popup.add(clearConsole);

        console.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        disabledPanel = new DisabledPanel(configPanel);
        add(new JScrollPane(disabledPanel), BorderLayout.PAGE_START);
        JScrollPane consoleScroll = new JScrollPane(console);
        consoleScroll.setBorder(BorderFactory.createTitledBorder("Console"));
        add(consoleScroll, BorderLayout.CENTER);
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
        configPanel.add(new JSeparator());
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
        final JComponent tablePanel =
                createTwinGroup(
                        Arrays.asList(createTagValueArgumentPanel(config.getTagValueArgs(),
                                "Additional Arguments")),
                        Arrays.asList(createTagValueArgumentPanel(config.getEnvironment(),
                                "Environment Variables")));
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        Action showMore = new AbstractAction() {
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
        };
        showMore.putValue(Action.NAME, "Show More");
        JCheckBox showMoreCheck = new JCheckBox(showMore);
        showMoreCheck.setSelected(false);
        showMoreCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(showMoreCheck);

        panel.add(tablePanel);

        tablePanel.setVisible(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        return panel;
    }

    JComponent createTagValueArgumentPanel(Map<String, String> tabeModelMap, String title) {
        final TagValueTableModel model = new TagValueTableModel(tabeModelMap);
        final JTable table = new JTable(model);
        JScrollPane pane = new JScrollPane(table);
        pane.setPreferredSize(new Dimension(0, 150));

        JPanel addDelPanel = new JPanel();
        addDelPanel.setLayout(new BoxLayout(addDelPanel, BoxLayout.Y_AXIS));
        addDelPanel.add(Box.createVerticalGlue());
        Action add = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField key = new JTextField(20);
                JTextField value = new JTextField(20);
                JComponent object =
                        createTwinGroup(Arrays.asList(new JLabel("Key"), new JLabel("Value")),
                                Arrays.asList(key, value));

                if (JOptionPane.showConfirmDialog(TaskPanel.this, object, "Input Key Value",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION
                        && !key.getText().isEmpty()) {
                    model.add(key.getText(), value.getText());
                }
            }
        };
        add.putValue(Action.SMALL_ICON, ADD);

        JButton butt = new JButton(add);
        butt.setBorderPainted(false);
        butt.setContentAreaFilled(false);
        addDelPanel.add(butt);

        Action del = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.remove(table.getSelectedRows());
            }
        };
        del.putValue(Action.SMALL_ICON, DELETE);
        butt = new JButton(del);
        butt.setBorderPainted(false);
        butt.setContentAreaFilled(false);

        addDelPanel.add(butt);
        addDelPanel.add(Box.createVerticalStrut(5));
        addDelPanel.add(Box.createVerticalGlue());

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(pane, BorderLayout.CENTER);
        panel.add(addDelPanel, BorderLayout.LINE_END);
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
                    .addComponent(item1Iter.next())
                    .addComponent(item2Iter.next()));
        }

        layout.setVerticalGroup(vGroup);
        groupedPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return groupedPanel;
    }

    private void execute() {
        config.save();

        final Map<ActionConfig, GitMavenAction> actionSq = new LinkedHashMap<>();
        for (ActionConfig actionCfg : config.getActionSequence()) {
            GitMavenAction action = ACTION_MAP.get(actionCfg.getActionName());

            if (action != null) {
                actionSq.put(actionCfg, action);
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
                } catch (Throwable e1) {
                    log.println("Unable to find Git repo @ " + config.getBaseDir().getAbsolutePath());
                    return;
                }
                try {
                    log.println("########### PROCESS BEGIN ###########");
                    for (Entry<ActionConfig, GitMavenAction> e : actionSq.entrySet()) {
                        ActionConfig actionCfg = e.getKey();
                        log.println("--------------- ACTION BEGIN : " + actionCfg.getActionName()
                                + " ------------------");
                        try {
                            if (!e.getValue().execute(utils, config, actionCfg, log)) {
                                log.print("FAILURE: execution failed -- action returned error status");
                                break;
                            }
                        } catch (Throwable e1) {
                            e1.printStackTrace(log);
                            break;
                        }

                        log.println("--------------- ACTION END : " + actionCfg.getActionName()
                                + " ------------------");
                        log.flush();
                    }
                    log.println("########### PROCESS ENDS ###########");
                } finally {
                    utils.utilCloseRepo();
                }
            }
        });
    }

    public ProcessConfig getConfig() {
        return config;
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
