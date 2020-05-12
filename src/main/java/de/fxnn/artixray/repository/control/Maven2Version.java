package de.fxnn.artixray.repository.control;

import de.fxnn.artixray.repository.boundary.Repository;
import de.fxnn.artixray.util.boundary.XmlDocuments;
import org.w3c.dom.Document;

import javax.xml.xpath.XPathExpressionException;

public class Maven2Version {

  private final String version;

  public Maven2Version(String version) {
    this.version = version;
  }

  public String resolveUsingMetadata(Document document) throws XPathExpressionException {
    if (isEmpty() || isLatestPlaceholder()) {
      return XmlDocuments.evaluateXPath(document, "/metadata/versioning/latest");
    }
    if (isReleasePlaceholder()) {
      return XmlDocuments.evaluateXPath(document, "/metadata/versioning/release");
    }
    return version;
  }

  public boolean isPlaceholderOrEmpty() {
    return isEmpty() || isReleasePlaceholder() ||isLatestPlaceholder();
  }

  public boolean isEmpty() {
    return version == null;
  }

  public boolean isReleasePlaceholder() {
    return Repository.RELEASE_PLACEHOLDER.equals(version);
  }

  public boolean isLatestPlaceholder() {
    return Repository.LATEST_PLACEHOLDER.equals(version);
  }
}
