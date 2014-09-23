/**
 *
 */
package org.apache.git.maven.task.gittask;

import java.io.PrintWriter;

import org.apache.git.maven.task.GitMavenAction;
import org.apache.git.maven.uiprops.ProcessConfig;
import org.kohsuke.MetaInfServices;

/**
 * @author p.mankala
 *
 */
@MetaInfServices
public class GitCommitAction extends GitMavenAction {

    @Override
    public boolean execute(GitActionUtils utils, final ProcessConfig cfg, final PrintWriter log) throws Throwable {
        utils.utilCommitAndPush(cfg.getArguments().get(COMMIT_MESSAGE), getCredentialProvider(cfg));
        return true;
    }

    @Override
    public String getActionName() {
        return "gitCommitAndPush";
    }
}
