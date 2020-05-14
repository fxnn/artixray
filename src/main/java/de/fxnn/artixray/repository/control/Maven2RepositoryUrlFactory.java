package de.fxnn.artixray.repository.control;

import de.fxnn.artixray.repository.boundary.ArtifactCoordinate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class Maven2RepositoryUrlFactory {

  /**
   * The basic URL of the repository, not terminated by a slash.
   * <p/>
   * E.g. {@code https://repo1.maven.org/maven2}
   */
  private final URL baseUrl;

  public Maven2RepositoryUrlFactory(URL baseUrl) {
    this.baseUrl = stripTrailingSlash(baseUrl);
  }

  public URL getBaseUrl() {
    return baseUrl;
  }

  public URL createUrlForArtifact(ArtifactCoordinate coordinate) {
    return new UrlBuilder()
        .appendGroupId(coordinate)
        .append(coordinate.getArtifactId())
        .append(coordinate.getVersion())
        .append(coordinate.toFileName())
        .toURL();
  }

  public URL createUrlForMetadata(ArtifactCoordinate coordinate) {
    return new UrlBuilder()
        .appendGroupId(coordinate)
        .append(coordinate.getArtifactId())
        .append("maven-metadata.xml")
        .toURL();
  }

  public URL createUrlForPath(String... pathComponents) throws IOException {
    UrlBuilder builder = new UrlBuilder();
    Arrays.stream(pathComponents).forEach(builder::append);
    return builder.toURL();
  }

  @Override
  public String toString() {
    return super.toString() + "{" + "baseUrl=" + baseUrl + '}';
  }

  private static URL stripTrailingSlash(URL baseUrl) {
    try {
      String baseUrlString = baseUrl.toExternalForm();
      if (baseUrlString.endsWith("/")) {
        return new URL(baseUrlString.substring(0, baseUrlString.length() - 1));
      }

      return baseUrl;
    } catch (MalformedURLException ex) {
      throw new RuntimeException("Could not strip trailing slash of " + baseUrl, ex);
    }
  }

  private class UrlBuilder {

    private final StringBuilder stringBuilder;

    private UrlBuilder() {
      stringBuilder = new StringBuilder(baseUrl.toExternalForm());
    }

    private UrlBuilder appendGroupId(ArtifactCoordinate artifactCoordinate) {
      return append(artifactCoordinate.getGroupId().split("\\."));
    }

    private UrlBuilder append(String... pathComponents) {
      Arrays.stream(pathComponents).forEach(this::append);
      return this;
    }

    private UrlBuilder append(String pathComponent) {
      stringBuilder.append("/"); // HINT: works, because baseUrl never ends with "/"
      stringBuilder.append(pathComponent);
      return this;
    }

    private URL toURL() {
      String urlString = stringBuilder.toString();
      try {
        return new URL(urlString);
      } catch (MalformedURLException e) {
        throw new IllegalStateException("Invalid URL: '" + urlString + "'", e);
      }
    }
  }
}
