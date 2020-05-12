package de.fxnn.artixray.repository.control;

import de.fxnn.artixray.archive.control.ArtifactCoordinate;
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
        Document document = XmlDocuments.parse(is);
        String resolvedVersion = version.resolveUsingMetadata(document);
        LOG.trace("For coordinate '{}', resolved version '{}' using repository '{}'", coordinate, resolvedVersion, url);
        return new ArtifactCoordinate(coordinate.getGroupId(), coordinate.getArtifactId(), coordinate.getType(),
            coordinate.getClassifier(), resolvedVersion);
      } catch (IOException e) {
        throw new IllegalStateException("Failed to download metadata for artifact '" + coordinate + "'", e);
      } catch (ParserConfigurationException | XPathExpressionException | SAXException e) {
        throw new IllegalStateException("Failed to parse metadata for artifact '" + coordinate + "'", e);
      }
    }

    return coordinate;
  }

  private InputStream openMetadataStream(ArtifactCoordinate coordinates) throws IOException {
    var pathBuilder = new StringBuilder();
    pathBuilder.append(coordinates.getGroupId().replaceAll("\\.", Repository.PATH_DELIMITER));
    pathBuilder.append(Repository.PATH_DELIMITER);
    pathBuilder.append(coordinates.getArtifactId());
    pathBuilder.append(Repository.PATH_DELIMITER);
    pathBuilder.append("maven-metadata.xml");
    return openStream(pathBuilder.toString());
  }

  @Override
  public InputStream openStream(ArtifactCoordinate coordinate) {
    return null;
  }

  @Override
  public InputStream openStream(String path) throws IOException {
    try {
      var builder = new StringBuilder(url.toExternalForm());
      if (!builder.toString().endsWith("/")) {
        builder.append("/");
      }
      builder.append(path);

      var url = new URL(builder.toString());
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
