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
    private List<String>              actionSequence;
    private File                      baseDir;
    private Map<String, String>       environment, tagValueArgs, arguments;

    public void setTitleId(String titleId) {
        this.titleId = titleId;
    }

    public String getTitleId() {
        return titleId;
    }

    public List<String> getActionSequence() {
        return actionSequence;
    }

    public void setActionSequence(List<String> actionSequence) {
        this.actionSequence = actionSequence;
    }

    public Map<String, String> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, String> arguments_) {
        Map<String, String> savedMap = getPrefMap("arguments", null);
        if (savedMap != null && arguments_ != null) {
            for (Entry<String, String> e : arguments_.entrySet()) {
                if (savedMap.containsKey(e.getKey())) {
                    e.setValue(savedMap.get(e.getKey()));
                }
            }
        }
        this.arguments = arguments_;
    }

    public File getBaseDir() {
        if (baseDir == null) {
            baseDir = new File(getPrefObj("baseDir", System.getProperty("user.dir")).toString());
        }

        return baseDir;
    }

    public void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
    }

    public void setTagValueArgs(Map<String, String> tagValueArgs_) {
        Map<String, String> savedMap = getPrefMap("tagValueArgs", null);
        if (savedMap != null && tagValueArgs_ != null) {
            for (Entry<String, String> e : tagValueArgs_.entrySet()) {
                if (savedMap.containsKey(e.getKey())) {
                    e.setValue(savedMap.get(e.getKey()));
                }
            }
        }
        this.tagValueArgs = tagValueArgs_;
    }

    public Map<String, String> getTagValueArgs() {
        if (tagValueArgs == null) {
            tagValueArgs = getPrefMap("tagValueArgs", null);
        }

        return tagValueArgs;
    }

    public Map<String, String> getEnvironment() {
        if (environment == null) {
            environment = getPrefMap("environment", null);
        }

        return environment;
    }

    public void setEnvironment(Map<String, String> environment_) {
        Map<String, String> savedMap = getPrefMap("environment", null);
        if (savedMap != null && environment_ != null) {
            for (Entry<String, String> e : environment_.entrySet()) {
                if (savedMap.containsKey(e.getKey())) {
                    e.setValue(savedMap.get(e.getKey()));
                }
            }
        }
        this.environment = environment_;
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

    List<String> getPrefList(String key, List<String> defaultMap) {
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

    public void save() {
        Preferences prefs = Preferences.userRoot().node(
                ProcessConfig.class.getName() + "." + titleId);

        if (baseDir != null) {
            prefs.put("baseDir", THREAD_LOCAL_XSTREAM.get().toXML(baseDir.getAbsolutePath()));
        }
        if (environment != null) {
            prefs.put("environment", THREAD_LOCAL_XSTREAM.get().toXML(environment));
        }
        if (tagValueArgs != null) {
            prefs.put("tagValueArgs", THREAD_LOCAL_XSTREAM.get().toXML(tagValueArgs));
        }
        if (arguments != null) {
            prefs.put("arguments", THREAD_LOCAL_XSTREAM.get().toXML(arguments));
        }
    }
}
