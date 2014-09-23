/**
 *
 */
package org.apache.git.maven;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.git.maven.uiprops.ProcessConfig;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
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
                    private final TaskPanel tp = new TaskPanel(cfg);
                    {
                        putValue(Action.NAME, cfg.getTitleId());
                        putValue(Action.SHORT_DESCRIPTION, cfg.getDescription());
                    }

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for (Component comp : viewPanel.getComponents()) {
                            if (comp instanceof TaskPanel) {
                                ((TaskPanel) comp).getConfig().save();
                            }
                        }

                        viewPanel.removeAll();
                        viewPanel.add(tp, BorderLayout.CENTER);
                        viewPanel.revalidate();
                        viewPanel.repaint();
                        viewPanel.requestFocusInWindow();
                        GitMavenMain.this.setTitle(cfg.getTitleId());
                    }
                });
            }

            actionPane.setToolTipText(e.getKey().getDescription());
            taskPaneContainer.add(actionPane);
        }


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // put the action list on the left
        add(taskPaneContainer, BorderLayout.LINE_START);

        // and a file browser in the middle
        add(viewPanel, BorderLayout.CENTER);

        initMenuBar();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e1) {
                saveWindowSetting();
                for (Entry<TaskGroup, List<ProcessConfig>> e : configMap.entrySet()) {
                    for (final ProcessConfig cfg : e.getValue()) {
                        cfg.save();
                    }
                }
            }
        });
    }

    void saveWindowSetting() {
        Preferences dimPrefs = Preferences.userRoot().node(ProcessConfig.class.getName());
        dimPrefs.putInt("height", this.getHeight());
        dimPrefs.putInt("width", this.getWidth());
        dimPrefs.putInt("x", this.getX());
        dimPrefs.putInt("y", this.getY());
    }

    void restoreWindowSetting() {
        Preferences dimPrefs = Preferences.userRoot().node(ProcessConfig.class.getName());
        setLocation(new Point(dimPrefs.getInt("x", 40), dimPrefs.getInt("y", 40)));
        setSize(dimPrefs.getInt("width", 700), dimPrefs.getInt("height", 500));
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

        /*
         * JMenu prefs = new JMenu("Preferences");
         * 
         * prefs.add(new AbstractAction() { { putValue(Action.NAME, "Maven ..."); }
         * 
         * @Override public void actionPerformed(ActionEvent e) {} });
         * 
         * prefs.add(new AbstractAction() { { putValue(Action.NAME, "Java ..."); }
         * 
         * @Override public void actionPerformed(ActionEvent e) {} });
         * 
         * menuBar.add(prefs);
         */

        setJMenuBar(menuBar);
    }

    public static void main(String[] args) {
        final Map<TaskGroup, List<ProcessConfig>> configMap = new LinkedHashMap<>();
        Yaml yaml = new Yaml();
        for (TaskGroup grp : TaskGroup.values()) {
            List<ProcessConfig> configs = new ArrayList<>();
            for (String file : grp.getConfFiles()) {
                String resource = "/" + grp.getBaseDir() + "/" + file;
                ProcessConfig config =
                        yaml.loadAs(GitMavenMain.class.getResourceAsStream(resource), ProcessConfig.class);

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

    private static void startInAWTThread(final Map<TaskGroup, List<ProcessConfig>> configMap) throws Throwable {
        GitMavenMain mainFrame = new GitMavenMain(configMap);
        mainFrame.pack();
        mainFrame.restoreWindowSetting();
        mainFrame.setVisible(true);
    }
}
