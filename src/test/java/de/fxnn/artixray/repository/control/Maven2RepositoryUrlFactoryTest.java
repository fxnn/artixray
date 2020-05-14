package de.fxnn.artixray.repository.control;

import de.fxnn.artixray.repository.boundary.ArtifactCoordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

class Maven2RepositoryUrlFactoryTest {

  private Maven2RepositoryUrlFactory sut;

  @Test
  void stripsTrailingSlash() throws Exception {

    givenRepository("https://example.com/");

    URL url = sut.getBaseUrl();

    Assertions.assertEquals("https://example.com", url.toExternalForm());

  }

  @Test
  void createUrlForPath() throws Exception {

    givenRepository("https://example.com/repo");

    URL url = sut.createUrlForPath("dir", "anotherDir", "file.txt");

    Assertions.assertEquals("https://example.com/repo/dir/anotherDir/file.txt", url.toExternalForm());

  }

  @Test
  void createUrlForArtifact() throws Exception {

    ArtifactCoordinate givenArtifact = new ArtifactCoordinate(
        "de.fxnn.artixray",
        "artixray",
        "zip",
        "docs",
        "1.0.0");
    givenRepository("https://repo1.maven.org/maven2");

    URL url = sut.createUrlForArtifact(givenArtifact);

    Assertions.assertEquals(
        "https://repo1.maven.org/maven2/de/fxnn/artixray/artixray/1.0.0/artixray-1.0.0-docs.zip",
        url.toExternalForm());

  }

  @Test
  void createUrlForMetadata() throws Exception {

    ArtifactCoordinate givenArtifact = new ArtifactCoordinate(
        "de.fxnn.artixray",
        "artixray",
        "zip",
        "docs",
        "1.0.0");
    givenRepository("https://repo1.maven.org/maven2");

    URL url = sut.createUrlForMetadata(givenArtifact);

    Assertions.assertEquals(
        "https://repo1.maven.org/maven2/de/fxnn/artixray/artixray/maven-metadata.xml",
        url.toExternalForm());
  }

  private void givenRepository(String urlString) throws MalformedURLException {
    sut = new Maven2RepositoryUrlFactory(new URL(urlString));
  }
}