package org.apache.git.maven.task;

import java.io.OutputStream;

import javax.swing.JComponent;

import org.kohsuke.MetaInfServices;

@MetaInfServices
public class GitStatusTask extends GitMavenTask {
    @Override
    public TaskGroup getTaskGroup() {
        return TaskGroup.GIT_TASK;
    }

    @Override
    public String getTaskName() {
        return "Show Git Status";
    }

    @Override
    public JComponent getTaskVisualization() {
        return null;
    }

    @Override
    public OutputStream executeTask() {
        return null;
    }
}
