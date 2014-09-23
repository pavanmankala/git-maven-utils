/**
 *
 */
package org.apache.git.maven.task.gittask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;

/**
 * @author p.mankala
 *
 */
public class GitActionUtils {
    private final Git myGit;

    public GitActionUtils(String gitBaseDir) throws IOException {
        File dotGitDir = new File(gitBaseDir + File.separator + ".git");
        if (dotGitDir.exists() && dotGitDir.isDirectory()) {
            myGit = Git.wrap(new FileRepositoryBuilder().setGitDir(dotGitDir).readEnvironment().findGitDir().build());
        } else {
            throw new RuntimeException("Not a git repo :(");
        }
    }

    public List<String> utilListBranches() {
        List<String> myBranchlist = new ArrayList<>();
        try {
            for (Ref ref : myGit.branchList().setListMode(ListMode.ALL).call()) {
                if (ref.isSymbolic()) {
                    continue;
                }

                myBranchlist.add(ref.getName());
            }
        } catch (GitAPIException e) {
            return Collections.emptyList();
        }

        return myBranchlist;
    }

    public RevCommit utilCommitAndPush(String message, CredentialsProvider credentials, boolean push,
            boolean askBeforePush) throws Throwable {
        myGit.add().addFilepattern(".").call();
        RevCommit commit = myGit.commit().setMessage(message).call();

        if (!push || (askBeforePush && !askForPush())) {
            return commit;
        }

        myGit.push().add("HEAD").setCredentialsProvider(credentials).call();
        return commit;
    }

    public RevCommit utilRevertLastCommitAndPush(CredentialsProvider credentials, boolean push, boolean askBeforePush)
            throws Throwable {
        RevCommit commit = myGit.revert().include(myGit.log().call().iterator().next()).call();

        if (!push || (askBeforePush && !askForPush())) {
            return commit;
        }

        myGit.push().add("HEAD").setCredentialsProvider(credentials).call();
        return commit;
    }

    public Ref utilCreateAndPushBranch(String branchName, CredentialsProvider credentials, boolean push,
            boolean askBeforePush) throws Throwable {
        Ref branchRef = myGit.branchCreate().setName(branchName).setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM).call();

        if (!push || (askBeforePush && !askForPush())) {
            return branchRef;
        }

        myGit.push().add(branchRef).setCredentialsProvider(credentials).call();
        return branchRef;
    }

    public Ref utilCreateAndPushTag(String tagName, CredentialsProvider credentials, boolean push, boolean askBeforePush)
            throws Throwable {
        Ref tagRef = myGit.tag().setName(tagName).call();

        if (!push || (askBeforePush && !askForPush())) {
            return tagRef;
        }

        myGit.push().add(tagRef).setCredentialsProvider(credentials).call();

        return tagRef;
    }

    private boolean askForPush() {
        return JOptionPane.showConfirmDialog(null, "Push changes to remote ?", "Confirm Push",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public boolean hasUncommitedChanges() {
        try {
            Status sts = myGit.status().call();
            return !sts.getUncommittedChanges().isEmpty();
        } catch (Throwable e) {
            return true;
        }
    }

    public void utilCloseRepo() {
        myGit.close();
    }

    public Git getGit() {
        return myGit;
    }
}
