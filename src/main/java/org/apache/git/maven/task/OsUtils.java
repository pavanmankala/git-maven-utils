package org.apache.git.maven.task;

public class OsUtils {
    private final static String OS;
    static {
        OS = System.getProperty("os.name");
    }

    public static boolean isWindows() {
        return OS.contains("Windows");
    }
}
