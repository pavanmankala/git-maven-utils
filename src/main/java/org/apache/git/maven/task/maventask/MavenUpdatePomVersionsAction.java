/**
 *
 */
package org.apache.git.maven.task.maventask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.git.maven.task.GitMavenAction;
import org.apache.git.maven.task.gittask.GitActionUtils;
import org.apache.git.maven.uiprops.ProcessConfig;
import org.kohsuke.MetaInfServices;

/**
 * @author p.mankala
 *
 */
@MetaInfServices
public class MavenUpdatePomVersionsAction extends GitMavenAction {
    @Override
    public boolean execute(GitActionUtils utils, final ProcessConfig cfg, final PrintWriter log) throws Throwable {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(new String[] {"mvn.bat", "versions:set",
                "-DnewVersion=" + cfg.getArguments().get(BRANCH_VERSION),
                "-DgenerateBackupPoms=false"});
        builder.directory(cfg.getBaseDir());
        builder.redirectErrorStream(true);
        Process process = builder.start();
        final InputStream is = process.getInputStream();

        new Thread(new Runnable() {
            public void run() {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    int word;
                    while ((word = br.read()) != -1) {
                        log.write(word);
                        log.flush();
                    }
                } catch (java.io.IOException e) {}
            }
        }).start();

        int returnCode = -1;
        try {
            returnCode = process.waitFor();
        } catch (InterruptedException ex) {}

        return returnCode == 0;
    }

    @Override
    public String getActionName() {
        return "mavenUpdatePomVersions";
    }
}
