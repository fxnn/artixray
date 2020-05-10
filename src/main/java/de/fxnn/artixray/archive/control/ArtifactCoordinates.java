package de.fxnn.artixray.archive.control;

import java.util.regex.Pattern;

public class ArtifactCoordinates {

  static final String COORDINATE_EXAMPLE = "groupId:artifactId[:type[:classifier]]:version";
  private static final Pattern COORDINATE_REGEX = Pattern.compile("([^:]+):([^:]+)(:([^:]+)(:([^:]+))?)?:([^:]+)");

  private final String groupId;
  private final String artifactId;
  private final String type;
  private final String classifier;
  private final String version;

  public ArtifactCoordinates(String groupId, String artifactId, String type, String classifier, String version) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.type = type;
    this.classifier = classifier;
    this.version = version;
  }

  public String getGroupId() {
    return groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public String getType() {
    return type;
  }

  public String getClassifier() {
    return classifier;
  }

  public String getVersion() {
    return version;
  }

  @Override
  public String toString() {
    var builder = new StringBuilder(groupId);
    builder.append(":").append(artifactId);
    if (type != null) {
      builder.append(":").append(type);
    }
    if (classifier != null) {
      builder.append(":").append(classifier);
    }
    builder.append(":").append(version);
    return builder.toString();
  }

  /**
   * Creates a new instance from a string.
   * @param coordinateString gives coordinates in the form {@link #COORDINATE_EXAMPLE}
   * @return
   */
  public static ArtifactCoordinates fromString(String coordinateString) {
    var matcher = COORDINATE_REGEX.matcher(coordinateString);
    if (matcher.matches()) {
      var groupId = matcher.group(1);
      var artifactId = matcher.group(2);
      var type = matcher.group(4);
      var classifier = matcher.group(6);
      var version = matcher.group(7);
      return new ArtifactCoordinates(groupId, artifactId, type, classifier, version);
    }

    throw new IllegalArgumentException("Invalid coordinate string: '" + coordinateString + "'. Expected a string of format '" + COORDINATE_EXAMPLE + "'");
  }
}
