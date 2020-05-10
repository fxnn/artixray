package de.fxnn.artixray.archive.control;

import de.fxnn.artixray.archive.boundary.Archive;
import de.fxnn.artixray.archive.boundary.ArchiveFile;
import de.fxnn.artixray.archive.boundary.ArchiveResolver;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@ApplicationScoped
public class DefaultArchiveResolver implements ArchiveResolver {

  private static final String REPO_URL_DEFAULT_VALUE = "https://repo1.maven.org/maven2/";

  @Inject
  @ConfigProperty(name = "artixray.repo.url", defaultValue = REPO_URL_DEFAULT_VALUE)
  String repoUrl;

  @Override
  public Archive resolve(String coordinatesString) {
    ArtifactCoordinates coordinates = ArtifactCoordinates.fromString(coordinatesString);
    if (coordinates.getVersion().equals("RELEASE")) {
      URL metadataUrl = createMetadataUrl(coordinates);
      try (InputStream is = metadataUrl.openStream()) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(is);
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile("/metadata/versioning/release");
        String version = expr.evaluate(document);
        return createDummyArchive(
            new ArtifactCoordinates(coordinates.getGroupId(), coordinates.getArtifactId(), coordinates.getType(),
                coordinates.getClassifier(), version));
      } catch (IOException e) {
        throw new IllegalStateException("Failed to download '" + metadataUrl + "' for artifact '" + coordinates + "'",
            e);
      } catch (ParserConfigurationException | XPathExpressionException | SAXException e) {
        throw new IllegalStateException(
            "Failed to parse metadata from '" + metadataUrl + "' for artifact '" + coordinates + "'", e);
      }
    }
    return createDummyArchive(coordinates);
  }

  private Archive createDummyArchive(ArtifactCoordinates coordinates) {
    return new Archive() {

      @Override
      public ArchiveFile readFile(String filePath) {
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(coordinates).append("\n");
        resultBuilder.append(filePath).append("\n");

        byte[] resultBytes = resultBuilder.toString().getBytes();
        ByteArrayInputStream resultStream = new ByteArrayInputStream(resultBytes);
        return new ArchiveFile(MediaType.TEXT_PLAIN, resultStream);
      }
    };
  }

  private URL createMetadataUrl(ArtifactCoordinates coordinates) {
    var builder = new StringBuilder();
    builder.append(repoUrl);
    if (!repoUrl.endsWith("/")) {
      builder.append("/");
    }
    builder.append(coordinates.getGroupId().replaceAll("\\.", "/"));
    builder.append("/");
    builder.append(coordinates.getArtifactId());
    builder.append("/maven-metadata.xml");
    try {
      return new URL(builder.toString());
    } catch (MalformedURLException e) {
      throw new IllegalStateException("Cannot create valid URL for artifact '" + coordinates + "'", e);
    }
  }
}
