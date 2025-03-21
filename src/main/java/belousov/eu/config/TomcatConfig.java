package belousov.eu.config;

import belousov.eu.controller.InfoController;
import belousov.eu.servlet.AuthServlet;
import belousov.eu.servlet.CharsetFilter;
import belousov.eu.servlet.ProfileServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import java.io.File;

public class TomcatConfig {

    private final Tomcat tomcat = new Tomcat();

    public TomcatConfig(DependencyContainer container) {
        tomcat.setPort(8080);

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

        Tomcat.addServlet(tomcatContext, "info", new InfoController());
        Tomcat.addServlet(tomcatContext, "auth", container.get(AuthServlet.class));
        Tomcat.addServlet(tomcatContext, "profile", container.get(ProfileServlet.class));
        tomcatContext.addServletMappingDecoded("/api/auth/*", "auth");
        tomcatContext.addServletMappingDecoded("/api/profile/*", "profile");


    }

    public void start() {
        try {
            tomcat.start();
            System.out.println("Server started on port 8080");
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            throw new RuntimeException(e); //TODO
        }
    }
}
