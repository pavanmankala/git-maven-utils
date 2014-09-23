/**
 *
 */
package org.apache.git.maven.task.maventask;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.git.maven.task.GitMavenAction;
import org.apache.git.maven.task.OsUtils;
import org.apache.git.maven.task.gittask.GitActionUtils;
import org.apache.git.maven.uiprops.ProcessConfig;
import org.apache.git.maven.uiprops.ProcessConfig.ActionConfig;
import org.kohsuke.MetaInfServices;

/**
 * @author p.mankala
 *
 */
@MetaInfServices
public class MavenCleanInstallAction extends GitMavenAction {
    @Override
    public boolean execute(GitActionUtils utils, final ProcessConfig cfg, ActionConfig actionCfg, final PrintWriter log)
            throws Throwable {
        ProcessBuilder builder = new ProcessBuilder();
        String m2Home = cfg.getArguments().get(MAVEN_HOME);
        if (m2Home == null) {
            m2Home = "";
        } else {
            File m2HomeF = new File(m2Home);
            if (m2HomeF.exists()) {
                m2HomeF = new File(m2HomeF, "bin");
            }
            if (!m2HomeF.exists()) {
                m2Home = "";
            } else {
                m2Home = m2HomeF.getAbsolutePath() + File.separator;
            }
        }

        builder.command(new String[] {m2Home + (OsUtils.isWindows() ? "mvn.bat" : "mvn"), "clean", "install"});
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
                } catch (java.io.IOException e) {
                }
            }
        }).start();

        int returnCode = -1;
        try {
            returnCode = process.waitFor();
        } catch (InterruptedException ex) {
        }

        if (returnCode != 0)
            throw new RuntimeException("Error execution maven process");
        else
            return true;
    }

    @Override
    public String getActionName() {
        return "mavenCleanInstall";
    }
}
