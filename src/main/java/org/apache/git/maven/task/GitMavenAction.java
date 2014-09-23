/**
 *
 */
package org.apache.git.maven.task;

import java.io.PrintWriter;

import org.apache.git.maven.task.gittask.GitActionUtils;
import org.apache.git.maven.uiprops.ProcessConfig;
import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.CredentialItem.CharArrayType;
import org.eclipse.jgit.transport.CredentialItem.Password;
import org.eclipse.jgit.transport.CredentialItem.StringType;
import org.eclipse.jgit.transport.CredentialItem.Username;
import org.eclipse.jgit.transport.CredentialItem.YesNoType;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;

/**
 * @author p.mankala
 *
 */

public abstract class GitMavenAction implements ConfigConstants {
    public abstract boolean execute(GitActionUtils utils, ProcessConfig cfg, PrintWriter log)
            throws Throwable;

    public abstract String getActionName();

    protected CredentialsProvider getCredentialProvider(final ProcessConfig cfg) {
        return new CredentialsProvider() {
            @Override
            public boolean supports(CredentialItem... items) {
                return true;
            }

            @Override
            public boolean isInteractive() {
                return true;
            }

            @Override
            public boolean get(URIish uri, CredentialItem... items) throws UnsupportedCredentialItem {
                for (CredentialItem item : items) {
                    if (item.isValueSecure()) {
                        PasswordDialog dialog = new PasswordDialog(item.getPromptText());
                        dialog.setVisible(true);
                        if (item instanceof StringType) {
                            ((StringType) item).setValue(new String(dialog.getPassword()));
                        } else if (item instanceof CharArrayType) {
                            ((CharArrayType) item).setValue(dialog.getPassword());
                        } else if (item instanceof Password) {
                            ((Password) item).setValue(dialog.getPassword());
                        }
                    } else {
                        if (item instanceof YesNoType) {
                            ((YesNoType) item).setValue(true);
                        } else if (item instanceof Username) {
                            ((Username) item).setValue(cfg.getArguments().get(USER_NAME));
                        } else if (item instanceof StringType) {
                            StringDialog dialog = new StringDialog(item.getPromptText());
                            dialog.setVisible(true);
                        }
                    }
                }
                return true;
            }
        };
    }
}
