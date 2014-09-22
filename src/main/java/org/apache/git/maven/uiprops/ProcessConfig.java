/**
 *
 */
package org.apache.git.maven.uiprops;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.prefs.Preferences;

import com.thoughtworks.xstream.XStream;

/**
 * @author p.mankala
 *
 */
public class ProcessConfig {
    final static ThreadLocal<XStream> THREAD_LOCAL_XSTREAM = new ThreadLocal<XStream>() {
                                                               protected XStream initialValue() {
                                                                   return new XStream();
                                                               }
                                                           };
    private String                    titleId;
    private String                    command;
    private File                      baseDir;
    private Map<String, String>       environment;
    private List<String>              programArgs;

    public void setTitleId(String titleId) {
        this.titleId = titleId;
    }

    public String getTitleId() {
        return titleId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public File getBaseDir() {
        if (baseDir == null) {
            baseDir = new File(getPrefString("baseDir", System.getProperty("user.dir")));
        }

        return baseDir;
    }

    public void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
    }

    public List<String> getProgramArgs() {
        if (programArgs == null) {
            programArgs = getPrefList("programArgs", null);
        }

        return programArgs;
    }

    public void setProgramArgs(List<String> programArgs) {
        this.programArgs = programArgs;
    }

    public Map<String, String> getEnvironment() {
        if (environment == null) {
            environment = getPrefMap("environment", null);
        }

        return environment;
    }

    public void setEnvironment(Map<String, String> environment) {
        this.environment = environment;
    }

    private String getPrefString(String key, String defaultVal) {
        Preferences prefs = Preferences.userRoot().node(
                ProcessConfig.class.getName() + "." + titleId);
        String prefVal;

        if ((prefVal = prefs.get(key, defaultVal)) == defaultVal) {
            prefs.put(key, prefVal);
        }

        return prefVal;
    }

    boolean getPrefBoolean(String key, boolean defaultVal) {
        Preferences prefs = Preferences.userRoot().node(
                ProcessConfig.class.getName() + "." + titleId);
        boolean prefVal;

        if ((prefVal = prefs.getBoolean(key, defaultVal)) == defaultVal) {
            prefs.putBoolean(key, prefVal);
        }

        return prefVal;
    }

    private Map<String, String> getPrefMap(String key, Map<String, String> defaultMap) {
        Object prefObj = getPrefObj(key, defaultMap);
        if (prefObj == null) {
            return Collections.emptyMap();
        } else {
            Map<String, String> retMap = new HashMap<>();

            if (prefObj instanceof Map) {
                Map<?, ?> readMap = (Map<?, ?>) prefObj;

                for (Entry<?, ?> e : readMap.entrySet()) {
                    retMap.put(e.getKey().toString(), e.getValue().toString());
                }
            }
            return retMap;
        }
    }

    private List<String> getPrefList(String key, List<String> defaultMap) {
        Object prefObj = getPrefObj(key, defaultMap);
        if (prefObj == null) {
            return Collections.emptyList();
        } else {
            List<String> retList = new ArrayList<>();

            if (prefObj instanceof List) {
                List<?> readList = (List<?>) prefObj;

                for (Object e : readList) {
                    retList.add(e.toString());
                }
            }

            return retList;
        }
    }

    private Object getPrefObj(String key, Object defaultObj) {
        try {
            Preferences prefs = Preferences.userRoot().node(
                    ProcessConfig.class.getName() + "." + titleId);
            String prefVal;

            if ((prefVal = prefs.get(key, null)) == null) {
                if (defaultObj != null) {
                    String defaultMapXml = THREAD_LOCAL_XSTREAM.get().toXML(defaultObj);
                    prefs.put(key, defaultMapXml);
                    return defaultObj;
                } else {
                    return null;
                }
            } else {
                return THREAD_LOCAL_XSTREAM.get().fromXML(prefVal);
            }
        } catch (Throwable e) {
            return defaultObj;
        }
    }
}
