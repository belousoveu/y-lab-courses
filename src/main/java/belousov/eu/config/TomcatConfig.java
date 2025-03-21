package belousov.eu.config;

import belousov.eu.servlet.CharsetFilter;
import belousov.eu.servlet.DispatcherServlet;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import java.io.File;

@Slf4j
public class TomcatConfig {

    private final Tomcat tomcat = new Tomcat();
    private static final int PORT = 8080;

    public TomcatConfig(DependencyContainer container) {
        tomcat.setPort(PORT);

        tomcat.setBaseDir(".");
        Connector connector = tomcat.getConnector();
        connector.setProperty("address", "0.0.0.0");

        Context tomcatContext = tomcat.addContext("", new File(".").getAbsolutePath());

        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName("CharsetFilter");
        filterDef.setFilterClass(CharsetFilter.class.getName());
        tomcatContext.addFilterDef(filterDef);

        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("CharsetFilter");
        filterMap.addURLPattern("/*");
        tomcatContext.addFilterMap(filterMap);

        Tomcat.addServlet(tomcatContext, "main", new DispatcherServlet(container));
        tomcatContext.addServletMappingDecoded("/api/*", "main");


    }

    public void start() {
        try {
            tomcat.start();
            log.info("Server Tomcat started on port {}", PORT);
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            log.error("Error starting Tomcat server", e);
        }
    }
}
