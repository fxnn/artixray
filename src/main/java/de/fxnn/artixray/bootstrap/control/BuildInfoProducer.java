package de.fxnn.artixray.bootstrap.control;

import de.fxnn.artixray.bootstrap.boundary.BuildInfo;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

public class BuildInfoProducer {

  public static final String MAVEN_PROPERTIES_RESOURCE_NAME = "maven.properties";

  @Produces
  @ApplicationScoped
  public BuildInfo createBuildInfoFromMavenProperties() {
    try (var is = openCclResource(MAVEN_PROPERTIES_RESOURCE_NAME)) {
      Properties properties = new Properties();
      properties.load(is);
      return new BuildInfo(
          properties.getProperty("project.groupId"),
          properties.getProperty("project.artifactId"),
          properties.getProperty("project.version"),
          properties.getProperty("project.url"));
    } catch (IOException e) {
      throw new IllegalStateException("Could not load build properties", e);
    }
  }

  private InputStream openCclResource(String resourceName) {
    ClassLoader ccl = Thread.currentThread().getContextClassLoader();
    InputStream is = ccl.getResourceAsStream(resourceName);
    if (is == null) {
      throw new IllegalStateException(
          "Could not find resource '" + resourceName + "' using '" + ccl + "'");
    }
    return is;
  }

}
