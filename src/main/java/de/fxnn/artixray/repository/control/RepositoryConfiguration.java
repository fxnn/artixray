package de.fxnn.artixray.repository.control;

import de.fxnn.artixray.repository.boundary.Repository;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;

public class RepositoryConfiguration {

  private static final String REPO_URL_DEFAULT_VALUE = "https://repo1.maven.org/maven2/";

  @Inject
  @ConfigProperty(name = "artixray.repo.url", defaultValue = REPO_URL_DEFAULT_VALUE)
  String repositoryUrl;

  @Produces
  @ApplicationScoped
  public Repository createRepository() {
    try {
      return new Maven2Repository(new URL(repositoryUrl));
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Illegal repository URL '"+repositoryUrl+"' configured", e);
    }
  }

}
