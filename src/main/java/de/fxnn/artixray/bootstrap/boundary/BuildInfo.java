package de.fxnn.artixray.bootstrap.boundary;

public class BuildInfo {

  private final String groupId;
  private final String artifactId;
  private final String version;
  private final String projectUrl;

  public BuildInfo(String groupId, String artifactId, String version, String projectUrl) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
    this.projectUrl = projectUrl;
  }

  public String getGroupId() {
    return groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public String getVersion() {
    return version;
  }

  public String getProjectUrl() {
    return projectUrl;
  }
}
