/**
 *
 */
package org.apache.git.maven;

import org.apache.git.maven.uiprops.ProcessConfig;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

/**
 * @author p.mankala
 *
 */
public class TestYamlCfg {
    @Test
    public void testProcessCfg() {
        Yaml yaml = new Yaml();
        ProcessConfig cfg = yaml.loadAs(
                TestYamlCfg.class.getResourceAsStream("/git+maven/mavenCreateBranch.yml"),
                ProcessConfig.class);
        System.out.println(cfg);
    }
}
