/**
 *
 */
package org.apache.git.maven;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.git.maven.task.ConfigConstants;
import org.apache.git.maven.task.gittask.GitActionUtils;
import org.apache.git.maven.task.maventask.MavenUpdatePomVersionsAction;
import org.apache.git.maven.uiprops.ProcessConfig;
import org.junit.Test;

/**
 * @author p.mankala
 *
 */
public class TestGitUtils {
    @Test
    public void testListAllBranches() {
        try {
            GitActionUtils util = new GitActionUtils("C:\\Opt\\Work\\git-maven-utils\\");
            for (String branch : util.utilListBranches()) {
                System.out.println(branch);
            }
            System.out.println(util.hasUncommitedChanges());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testChangeVersionNumbers() {
        MavenUpdatePomVersionsAction action = new MavenUpdatePomVersionsAction();
        ProcessConfig cfg = new ProcessConfig();
        Map<String, String> map = new HashMap<>();
        map.put(ConfigConstants.BRANCH_VERSION, "1.1-SNAPSHOT");
        cfg.setArguments(map);
        cfg.setBaseDir(new File("C:\\Opt\\Work\\git-maven-utils\\"));
        try {
            action.execute(new GitActionUtils("C:\\Opt\\Work\\git-maven-utils\\"), cfg, new PrintWriter(System.out));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
