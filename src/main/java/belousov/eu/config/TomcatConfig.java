package belousov.eu.config;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class TomcatConfig {

    private final Tomcat tomcat = new Tomcat();
    private final Context tomcatContext;

    public TomcatConfig() {
        tomcat.setPort(8080);
        tomcat.setBaseDir(".");
        this.tomcatContext = tomcat.addContext("", new File(".").getAbsolutePath());


    }

    public void start() {
        try {
            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            throw new RuntimeException(e); //TODO
        }
    }
}
