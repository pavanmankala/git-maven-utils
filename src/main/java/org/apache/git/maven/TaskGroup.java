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
    GIT("git", Arrays.asList("gitLog.yml"), "Git Functions",
            "Display all available functions specifically releate to Git"),

    MAVEN("maven", Arrays.asList("mavenCompile.yml", "mavenChangeVersions.yml"), "Maven Functions",
            "Display all available functions specifically releate to Maven"),

    GIT_MAVEN("git+maven", Arrays.asList("mavenCreateBranchNoPush.yml", "mavenCreateTagNoPush.yml",
            "mavenCreateBranch.yml", "mavenTag.yml"), "Maven + Git Functions",
            "Display all available functions for SCM=Git and BuildSystem=Maven"), ;

    private final List<String> confFiles;
    private final String       baseDir, displayName, description;

    private TaskGroup(String baseDir, List<String> confFiles, String displayName, String description) {
        this.baseDir = baseDir;
        this.confFiles = Collections.unmodifiableList(confFiles);
        this.displayName = displayName;
        this.description = description;
    }

    public String getDescription() {
        return description;
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
