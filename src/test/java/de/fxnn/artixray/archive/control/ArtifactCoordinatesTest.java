package de.fxnn.artixray.archive.control;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ArtifactCoordinatesTest {

  @Test
  public void acceptsMinimumExampleCoordinates() {

    var givenString = createMinimumExampleCoordinates();

    ArtifactCoordinates sut = ArtifactCoordinates.fromString(givenString);

    Assertions.assertEquals(sut.getGroupId(), "groupId");
    Assertions.assertEquals(sut.getArtifactId(), "artifactId");
    Assertions.assertEquals(sut.getVersion(), "version");

    Assertions.assertNull(sut.getClassifier());
    Assertions.assertNull(sut.getType());

    Assertions.assertEquals(givenString, sut.toString());

  }

  @Test
  public void acceptsMaximumExampleCoordinates() {

    var givenString = createMaximumExampleCoordinates();

    ArtifactCoordinates sut = ArtifactCoordinates.fromString(givenString);

    Assertions.assertEquals(sut.getGroupId(), "groupId");
    Assertions.assertEquals(sut.getArtifactId(), "artifactId");
    Assertions.assertEquals(sut.getClassifier(), "classifier");
    Assertions.assertEquals(sut.getType(), "type");
    Assertions.assertEquals(sut.getVersion(), "version");

    Assertions.assertEquals(givenString, sut.toString());

  }

  private String createMaximumExampleCoordinates() {
    return ArtifactCoordinates.COORDINATE_EXAMPLE
        .replace("[", "")
        .replace("]", "");
  }

  private String createMinimumExampleCoordinates() {
    var result = ArtifactCoordinates.COORDINATE_EXAMPLE;
    var unmodified = false;
    while (!unmodified) {
      var modification = result.replaceFirst("\\[[^\\[\\]]+]", "");
      unmodified = result.equals(modification);
      result = modification;
    }
    return result;
  }

}