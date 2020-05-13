package de.fxnn.artixray.repository.control;

import de.fxnn.artixray.repository.boundary.Repository;

public class Maven2Version {

  private final String version;

  public Maven2Version(String version) {
    this.version = version;
  }

  public String getVersion() {
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
