/**
 *
 */
package org.apache.git.maven;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.git.maven.uiprops.ProcessConfig;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel;
import org.yaml.snakeyaml.Yaml;


/**
 * @author p.mankala
 *
 */
public class GitMavenMain extends JXFrame {
    private final JPanel viewPanel;

    public GitMavenMain(final Map<TaskGroup, List<ProcessConfig>> configMap) {
        this.viewPanel = new JPanel(new BorderLayout());

        // a container to put all JXTaskPane together
        JXTaskPaneContainer taskPaneContainer = new JXTaskPaneContainer();

        for (Entry<TaskGroup, List<ProcessConfig>> e : configMap.entrySet()) {
            JXTaskPane actionPane = new JXTaskPane();
            actionPane.setTitle(e.getKey().getDisplayName());
            for (final ProcessConfig cfg : e.getValue()) {
                actionPane.add(new AbstractAction() {
                    private TaskPanel tp;

                    {
                        putValue(Action.NAME, cfg.getTitleId());
                        tp = new TaskPanel(cfg);
                    }

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        tp = new TaskPanel(cfg);
                        viewPanel.removeAll();
                        viewPanel.add(tp, BorderLayout.CENTER);
                        viewPanel.revalidate();
                        viewPanel.requestFocusInWindow();
                    }
                });
            }

            taskPaneContainer.add(actionPane);
        }


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // put the action list on the left
        add(taskPaneContainer, BorderLayout.LINE_START);

        // and a file browser in the middle
        add(viewPanel, BorderLayout.CENTER);

        initMenuBar();
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        file.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Exit");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menuBar.add(file);

        JMenu prefs = new JMenu("Preferences");

        prefs.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Maven ...");
            }

            @Override
            public void actionPerformed(ActionEvent e) {}
        });

        prefs.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Java ...");
            }

            @Override
            public void actionPerformed(ActionEvent e) {}
        });

        menuBar.add(prefs);

        setJMenuBar(menuBar);
    }

    public static void main(String[] args) {
        final Map<TaskGroup, List<ProcessConfig>> configMap = new LinkedHashMap<>();
        Yaml yaml = new Yaml();
        for (TaskGroup grp : TaskGroup.values()) {
            List<ProcessConfig> configs = new ArrayList<>();
            for (String file : grp.getConfFiles()) {
                String resource = "/" + grp.getBaseDir() + "/" + file;
                ProcessConfig config = yaml.loadAs(
                        GitMavenMain.class.getResourceAsStream(resource),
                        ProcessConfig.class);

                if (config != null) {
                    configs.add(config);
                }
            }

            if (!configs.isEmpty())
                configMap.put(grp, configs);
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    startInAWTThread(configMap);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void startInAWTThread(final Map<TaskGroup, List<ProcessConfig>> configMap)
            throws Throwable {
        GitMavenMain mainFrame = new GitMavenMain(configMap);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
}
