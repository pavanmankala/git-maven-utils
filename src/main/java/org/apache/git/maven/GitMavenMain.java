/**
 *
 */
package org.apache.git.maven;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel;


/**
 * @author p.mankala
 *
 */
public class GitMavenMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    startInAWTThread();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void startInAWTThread() throws Throwable {
        UIManager.setLookAndFeel(new SubstanceGraphiteGlassLookAndFeel());
        JXFrame frame = new JXFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // a container to put all JXTaskPane together
        JXTaskPaneContainer taskPaneContainer = new JXTaskPaneContainer();

        // create a first taskPane with common actions
        JXTaskPane actionPane = new JXTaskPane();
        actionPane.setTitle("Files and Folders");

        // actions can be added, a hyperlink will be created
        actionPane.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Action 1");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        actionPane.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Action 2");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        // add this taskPane to the taskPaneContainer
        taskPaneContainer.add(actionPane);

        // create another taskPane, it will show details of the selected file
        JXTaskPane details = new JXTaskPane();
        details.setTitle("Details");

        // add standard components to the details taskPane
        JLabel searchLabel = new JLabel("Search:");
        JTextField searchField = new JTextField("");
        details.add(searchLabel);
        details.add(searchField);

        taskPaneContainer.add(details);


        // put the action list on the left
        frame.add(taskPaneContainer, BorderLayout.LINE_START);

        // and a file browser in the middle
        frame.add(new JFileChooser(), BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

}
