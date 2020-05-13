package de.fxnn.artixray.repository.boundary;

import java.util.regex.Pattern;

public class ArtifactCoordinate {

  static final String COORDINATE_EXAMPLE = "groupId:artifactId[[:type[:classifier]]:version]";
  private static final Pattern COORDINATE_REGEX = Pattern.compile("([^:]+):([^:]+)((:([^:]+)(:([^:]+))?)?:([^:]+))?");

  private final String groupId;
  private final String artifactId;
  private final String type;
  private final String classifier;
  private final String version;

  public ArtifactCoordinate(String groupId, String artifactId, String type, String classifier, String version) {
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

  public String toFileName() {
    var builder = new StringBuilder(artifactId);
    builder.append("-").append(version);
    if (classifier != null) {
      builder.append("-").append(classifier);
    }
    builder.append(".").append(type);
    return builder.toString();
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
    if (version != null) {
      builder.append(":").append(version);
    }
    return builder.toString();
  }

  /**
   * Creates a new instance from a string.
   * @param coordinateString gives coordinates in the form {@link #COORDINATE_EXAMPLE}
   * @return
   */
  public static ArtifactCoordinate fromString(String coordinateString) {
    var matcher = COORDINATE_REGEX.matcher(coordinateString);
    if (matcher.matches()) {
      var groupId = matcher.group(1);
      var artifactId = matcher.group(2);
      var type = matcher.group(5);
      var classifier = matcher.group(7);
      var version = matcher.group(8);
      return new ArtifactCoordinate(groupId, artifactId, type, classifier, version);
    }

    throw new IllegalArgumentException("Invalid coordinate string: '" + coordinateString + "'. Expected a string of format '" + COORDINATE_EXAMPLE + "'");
  }
}