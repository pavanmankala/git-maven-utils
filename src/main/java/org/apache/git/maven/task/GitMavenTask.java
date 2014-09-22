package org.apache.git.maven.task;

import java.io.OutputStream;

import javax.swing.JComponent;

public abstract class GitMavenTask {
    public enum TaskGroup {
        GIT_TASK, MAVEN_TASK, GIT_MAVEN_TASK
    }

    public abstract TaskGroup getTaskGroup();

    public abstract String getTaskName();

    public abstract JComponent getTaskVisualization();

    public abstract OutputStream executeTask();
}
