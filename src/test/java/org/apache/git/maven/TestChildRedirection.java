package org.apache.git.maven;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.CredentialItem.StringType;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;
import org.junit.Test;

public class TestChildRedirection {

    // @Test
    public void testChildRedirection() {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(new String[] {"git", "log"});
        builder.directory(new File("/home/mankala/work/eclipse-luna-jee/git-maven-utils"));
        builder.redirectErrorStream(true);
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException ex) {
            // --
        }
        OutputStream os = process.getOutputStream();
        final PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)));

        final InputStream is = process.getInputStream();
        new Thread(new Runnable() {
            public void run() {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (java.io.IOException e) {
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int i = -1;
                    while ((i = System.in.read()) != -1) {
                        pw.write(i);
                        pw.flush();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                pw.close();
            }
        }) {
            {
                setDaemon(true);
            };
        }.start();

        int returnCode = -1;
        try {
            returnCode = process.waitFor();
        } catch (InterruptedException ex) {
            // --
        }
        System.out.println("Exit code" + returnCode);
    }

    //@Test
    public void testGitLogCommand() {
        FileRepositoryBuilder frb = new FileRepositoryBuilder();
        try {
            Repository gitRepo =
                    frb.setGitDir(new File("/home/mankala/work/eclipse-luna-jee/git-maven-utils/.git"))
                            .readEnvironment().findGitDir().build();
            Git git = Git.wrap(gitRepo);
            for (RevCommit commit : git.log().call()) {
                System.out.println(commit + "-- " + commit.getCommitterIdent());
            }

            for (Ref r : git.branchList().call()) {
                System.out.println(r);
            }
            Ref branchRef =
                    git.branchCreate().setName("myBranch").setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM).call();
            git.push().add(branchRef).setCredentialsProvider(new CredentialsProvider() {
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
                        System.out.println(item.getPromptText());
                        if (item instanceof StringType) {
                            ((StringType) item).setValue("Kartik.com1!");
                        }
                    }
                    return true;
                }
            }).call();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGitRevertCommand() {
        FileRepositoryBuilder frb = new FileRepositoryBuilder();
        try {
            Repository gitRepo =
                    frb.setGitDir(new File("/home/mankala/work/eclipse-luna-jee/git-maven-utils/.git"))
                            .readEnvironment().findGitDir().build();
            Git git = Git.wrap(gitRepo);
            RevCommit c = git.log().call().iterator().next();
            RevCommit newCommit = git.revert().include(c).call();
            System.out.println(newCommit);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TestChildRedirection tcr = new TestChildRedirection();
        tcr.testChildRedirection();
    }
}
