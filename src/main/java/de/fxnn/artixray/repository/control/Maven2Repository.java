package de.fxnn.artixray.repository.control;

import de.fxnn.artixray.repository.boundary.ArtifactCoordinate;
import de.fxnn.artixray.repository.boundary.Repository;
import de.fxnn.artixray.util.boundary.XmlDocuments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Maven2Repository implements Repository {

  private static final Logger LOG = LoggerFactory.getLogger(Maven2Repository.class);
  private final URL url;

  public Maven2Repository(URL url) {
    this.url = url;
  }

  @Override
  public ArtifactCoordinate resolveVersion(ArtifactCoordinate coordinate) {
    Maven2Version version = new Maven2Version(coordinate.getVersion());
    if (version.isPlaceholderOrEmpty()) {
      try (InputStream is = openMetadataStream(coordinate)) {
        String resolvedVersion = resolveVersion(version, is);
        LOG.trace("For coordinate '{}', resolved version '{}' using repository '{}'", coordinate, resolvedVersion, url);
        return new ArtifactCoordinate(coordinate.getGroupId(), coordinate.getArtifactId(), coordinate.getType(),
            coordinate.getClassifier(), resolvedVersion);
      } catch (IOException e) {
        throw new IllegalStateException("Failed to download metadata for artifact '" + coordinate + "'", e);
      }
    }

    return coordinate;
  }

  private String resolveVersion(Maven2Version version, InputStream xmlMetadataStream) {
    try {
      Document metadataDocument = XmlDocuments.parse(xmlMetadataStream);
      if (version.isEmpty() || version.isLatestPlaceholder()) {
        return XmlDocuments.evaluateXPath(metadataDocument, "/metadata/versioning/latest");
      }
      if (version.isReleasePlaceholder()) {
        return XmlDocuments.evaluateXPath(metadataDocument, "/metadata/versioning/release");
      }
      return version.getVersion();

    } catch (IOException | ParserConfigurationException | SAXException e) {
      throw new IllegalStateException("Failed to parse metadata", e);
    } catch (XPathExpressionException ex) {
      throw new IllegalStateException("Failed to evaluate XPath expression", ex);
    }
  }

  private InputStream openMetadataStream(ArtifactCoordinate coordinates) throws IOException {
    return openStream(createPathRelativeToGroupIdAndArtifactId(coordinates, "maven-metadata.xml"));
  }

  @Override
  public InputStream openStream(ArtifactCoordinate coordinate) throws IOException {
    return openStream(createPathRelativeToGroupIdAndArtifactId(coordinate, coordinate.getVersion(), coordinate.toFileName()));
  }

  private String[] createPathRelativeToGroupIdAndArtifactId(ArtifactCoordinate coordinates, String... pathComponents) {
    List<String> path = new ArrayList<>(Arrays.asList(coordinates.getGroupId().split("\\.")));
    path.add(coordinates.getArtifactId());
    path.addAll(Arrays.asList(pathComponents));
    return path.toArray(new String[0]);
  }

  @Override
  public InputStream openStream(String... pathComponents) throws IOException {
    var builder = new StringBuilder(url.toExternalForm());
    if (builder.toString().endsWith("/")) {
      builder.deleteCharAt(builder.length() - 1);
    }
    for (String pathComponent : pathComponents) {
      builder.append("/").append(pathComponent);
    }
    String path = builder.toString();

    try {
      var url = new URL(path);
      return openStream(url);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Cannot create valid URL for path '" + path + "'", e);
    }
  }

  private InputStream openStream(URL url) throws IOException {
    LOG.trace("Opening stream for URL '{}'", url);
    return url.openStream();
  }
}
