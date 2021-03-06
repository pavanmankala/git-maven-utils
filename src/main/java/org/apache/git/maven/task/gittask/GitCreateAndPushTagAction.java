/**
 *
 */
package org.apache.git.maven.task.gittask;

import java.io.PrintWriter;

import org.apache.git.maven.task.GitMavenAction;
import org.apache.git.maven.uiprops.ProcessConfig;
import org.apache.git.maven.uiprops.ProcessConfig.ActionConfig;
import org.kohsuke.MetaInfServices;

/**
 * @author p.mankala
 *
 */
@MetaInfServices
public class GitCreateAndPushTagAction extends GitMavenAction {

    @Override
    public boolean execute(GitActionUtils utils, final ProcessConfig cfg, ActionConfig actionCfg, final PrintWriter log)
            throws Throwable {
        if (utils.hasUncommitedChanges()) {
            log.write("has uncommitted changes... exiting");
            return false;
        }
        boolean promptForPush = getExtraParam(Boolean.class, actionCfg, "promptBeforePush") == Boolean.TRUE;
        boolean push = getExtraParam(Boolean.class, actionCfg, "push") == Boolean.TRUE;

        utils.utilCreateAndPushTag(cfg.getArguments().get(TAG_NAME), getCredentialProvider(cfg), push, promptForPush);

        return true;
    }

    @Override
    public String getActionName() {
        return "gitCreateAndPushTag";
    }
}
