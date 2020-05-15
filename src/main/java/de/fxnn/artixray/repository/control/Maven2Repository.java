package de.fxnn.artixray.repository.control;

import de.fxnn.artixray.repository.boundary.ArtifactCoordinate;
import de.fxnn.artixray.repository.boundary.Repository;
import de.fxnn.artixray.util.boundary.XmlDocuments;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Maven2Repository implements Repository {

  private static final Logger LOG = LoggerFactory.getLogger(Maven2Repository.class);
  private static final String DEFAULT_TYPE = "jar";

  private final Maven2RepositoryUrlFactory urlFactory;

  public Maven2Repository(URL baseUrl) {
    this.urlFactory = new Maven2RepositoryUrlFactory(baseUrl);
  }

  @Override
  public ArtifactCoordinate resolveCoordinate(ArtifactCoordinate coordinate) {
    Maven2Version version = new Maven2Version(coordinate.getVersion());
    if (version.isPlaceholderOrEmpty()) {
      try (InputStream is = openMetadataStream(coordinate)) {
        version = new Maven2Version(resolveVersion(version, is));
        LOG.trace("For coordinate '{}', resolved version '{}' using repository '{}'", coordinate,
            version,
            getBaseUrl());
      } catch (IOException e) {
        throw new IllegalStateException(
            "Failed to download metadata for artifact '" + coordinate + "'", e);
      }
    }

    String type = coordinate.getType();
    if (type == null) {
      type = DEFAULT_TYPE;
    }

    return new ArtifactCoordinate(coordinate.getGroupId(), coordinate.getArtifactId(), type,
        coordinate.getClassifier(), version.toString());
  }

  private URL getBaseUrl() {
    return urlFactory.getBaseUrl();
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
      return version.toString();

    } catch (IOException | ParserConfigurationException | SAXException e) {
      throw new IllegalStateException("Failed to parse metadata", e);
    } catch (XPathExpressionException ex) {
      throw new IllegalStateException("Failed to evaluate XPath expression", ex);
    }
  }

  private InputStream openMetadataStream(ArtifactCoordinate coordinate) throws IOException {
    return openStream(urlFactory.createUrlForMetadata(coordinate));
  }

  @Override
  public InputStream openStream(ArtifactCoordinate coordinate) throws IOException {
    return openStream(urlFactory.createUrlForArtifact(coordinate));
  }

  @Override
  public InputStream openStream(String... pathComponents) throws IOException {
    return openStream(urlFactory.createUrlForPath(pathComponents));
  }

  private InputStream openStream(URL url) throws IOException {
    LOG.trace("Opening stream for URL '{}'", url);
    return url.openStream();
  }
}
