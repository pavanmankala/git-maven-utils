/**
 *
 */
package org.apache.git.maven;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.CredentialItem.Password;
import org.eclipse.jgit.transport.CredentialItem.StringType;
import org.eclipse.jgit.transport.CredentialItem.YesNoType;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;
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
        File localPath = File.createTempFile("TestGitRepository", "");
        localPath.delete();
        String REMOTE_URL = "git@github.com:pavanmankala/git-maven-utils.git";
        // then clone
        System.out.println("Cloning from " + REMOTE_URL + " to " + localPath);
        Git.cloneRepository().setURI(REMOTE_URL).setDirectory(localPath)
                .setCredentialsProvider(new CredentialsProvider() {
                    @Override
                    public boolean supports(CredentialItem... items) {
                        return false;
                    }

                    @Override
                    public boolean isInteractive() {
                        return false;
                    }

                    @Override
                    public boolean get(URIish uri, CredentialItem... items) throws UnsupportedCredentialItem {
                        for (CredentialItem item : items) {
                            if (item instanceof YesNoType) {
                                ((YesNoType) item).setValue(true);
                            }
                            if (item instanceof Password) {
                                ((Password) item).setValue("Shanmukh".toCharArray());
                            }
                            if (item instanceof StringType) {
                                ((StringType) item).setValue("Kartik.com1!");
                            }
                        }
                        return true;
                    }
                }).call();
        // now open the created repository
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(localPath).readEnvironment() // scan environment
                                                                               // GIT_* variables
                .findGitDir() // scan up the file system tree
                .build();
        System.out.println("Having repository: " + repository.getDirectory());
        repository.close();

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
            public void actionPerformed(ActionEvent e) {}
        });
        actionPane.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Action 2");
            }

            @Override
            public void actionPerformed(ActionEvent e) {}
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
