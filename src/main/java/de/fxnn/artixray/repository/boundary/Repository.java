package de.fxnn.artixray.repository.boundary;

import de.fxnn.artixray.archive.control.ArtifactCoordinate;

import java.io.IOException;
import java.io.InputStream;

public interface Repository {

  String PATH_DELIMITER = "/";
  String RELEASE_PLACEHOLDER = "RELEASE";
  String LATEST_PLACEHOLDER = "LATEST";

  /**
   * Resolves missing or placeholder version fields in the given {@code coordinate}, if any.
   * <p/>
   * Versions are resolved using this repositories metadata. This might involve downloading an XML file from the
   * repository.
   *
   * @param coordinate An {@link ArtifactCoordinate} that may have a version field containing {@code null},
   *                   {@link #RELEASE_PLACEHOLDER} or {@link #LATEST_PLACEHOLDER}.
   *
   * @return An {@link ArtifactCoordinate} with a version field that is not {@code null}, nor one of the placeholder
   *         values.
   */
  ArtifactCoordinate resolveVersion(ArtifactCoordinate coordinate);

  /**
   * Opens a stream for a file in this repository.
   * @param coordinate Coordinate for the requested file.
   * @return An {@link InputStream} to the requested file.
   */
  InputStream openStream(ArtifactCoordinate coordinate);

  /**
   * Opens a stream for a file in this repository.
   * @param path Path to the requested file. Individual path compoments are to be separated using
   *             {@link #PATH_DELIMITER}. The path must not start with a delimiter.
   * @return An {@link InputStream} to the requested file.
   */
  InputStream openStream(String path) throws IOException;

}
