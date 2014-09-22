/**
 *
 */
package org.apache.git.maven;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author p.mankala
 *
 */
public enum TaskGroup {
    GIT("git", Arrays.asList("gitLog.yml"), "Git Functions"),
    MAVEN("maven", Arrays.asList("mavenCompile.yml"), "Maven Functions"),
    GIT_MAVEN("git+maven", Arrays.asList("mavenCreateBranchOrTag.yml"), "Git+Maven Functions"), ;

    private final List<String> confFiles;
    private final String       baseDir, displayName;

    private TaskGroup(String baseDir, List<String> confFiles, String displayName) {
        this.baseDir = baseDir;
        this.confFiles = Collections.unmodifiableList(confFiles);
        this.displayName = displayName;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public List<String> getConfFiles() {
        return confFiles;
    }

    public String getDisplayName() {
        return displayName;
    }
}
