package org.apache.git.maven.task.gittask;

import java.io.PrintWriter;

import org.apache.git.maven.task.GitMavenAction;
import org.apache.git.maven.uiprops.ProcessConfig;
import org.apache.git.maven.uiprops.ProcessConfig.ActionConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class GitLogAction extends GitMavenAction {
    @Override
    public boolean execute(GitActionUtils utils, ProcessConfig cfg, ActionConfig actionCfg, PrintWriter log)
            throws Throwable {
        for (RevCommit commit : utils.getGit().log().call()) {
            log.println("Commit SHA1: " + commit.getName());
            log.println("Time: " + commit.getCommitterIdent().getWhen());
            log.println("Committer: " + commit.getCommitterIdent().getName() + "<"
                    + commit.getCommitterIdent().getEmailAddress() + ">");
            log.println("Message: " + commit.getFullMessage());
            log.println();
        }
        return true;
    }

    @Override
    public String getActionName() {
        return "gitLog";
    }
}
