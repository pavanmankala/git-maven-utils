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
    public boolean execute(GitActionUtils utils, final ProcessConfig cfg, final PrintWriter log)
            throws Throwable {
        String message;
        if (cfg.getArguments().containsKey(BRANCH_NAME)) {
            message = "Creating Branch - " + cfg.getArguments().get(BRANCH_NAME) + ":"
                    + cfg.getArguments().get(BRANCH_VERSION);
        } else {
            message = "Creating Tag - " + cfg.getArguments().get(TAG_NAME) + ":"
                    + cfg.getArguments().get(TAG_VERSION);
        }
        utils.utilCommitAndPush(message, getCredentialProvider(cfg));
        return true;
    }

    @Override
    public String getActionName() {
        return "gitCommitAndPush";
    }
}
