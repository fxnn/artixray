package de.fxnn.artixray.archive.control;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ArtifactCoordinateTest {

  @Test
  public void acceptsMinimumExampleCoordinates() {

    var givenString = createMinimumExampleCoordinates();

    ArtifactCoordinate sut = ArtifactCoordinate.fromString(givenString);

    Assertions.assertEquals("groupId", sut.getGroupId());
    Assertions.assertEquals("artifactId", sut.getArtifactId());

    Assertions.assertNull(sut.getClassifier());
    Assertions.assertNull(sut.getType());
    Assertions.assertNull(sut.getVersion());

    Assertions.assertEquals(givenString, sut.toString());

  }

  @Test
  public void acceptsMaximumExampleCoordinates() {

    var givenString = createMaximumExampleCoordinates();

    ArtifactCoordinate sut = ArtifactCoordinate.fromString(givenString);

    Assertions.assertEquals("groupId", sut.getGroupId());
    Assertions.assertEquals("artifactId", sut.getArtifactId());
    Assertions.assertEquals("classifier", sut.getClassifier());
    Assertions.assertEquals("type", sut.getType());
    Assertions.assertEquals("version", sut.getVersion());

    Assertions.assertEquals(givenString, sut.toString());

  }

  private String createMaximumExampleCoordinates() {
    return ArtifactCoordinate.COORDINATE_EXAMPLE
        .replace("[", "")
        .replace("]", "");
  }

  private String createMinimumExampleCoordinates() {
    var result = ArtifactCoordinate.COORDINATE_EXAMPLE;
    var unmodified = false;
    while (!unmodified) {
      var modification = result.replaceFirst("\\[[^\\[\\]]+]", "");
      unmodified = result.equals(modification);
      result = modification;
    }
    return result;
  }

}