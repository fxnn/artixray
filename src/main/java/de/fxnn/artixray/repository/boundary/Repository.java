package de.fxnn.artixray.repository.boundary;

import java.io.IOException;
import java.io.InputStream;

public interface Repository {

  String RELEASE_PLACEHOLDER = "RELEASE";
  String LATEST_PLACEHOLDER = "LATEST";

  /**
   * Resolves missing or placeholder fields in the given {@code coordinate}, if any.
   * <p/>
   * Versions are resolved using this repositories metadata. This might involve downloading an XML
   * file from the repository.
   * <p/>
   * A missing type is replaced by a default, probably {@code jar}.
   *
   * @param coordinate An {@link ArtifactCoordinate} that may have a version field containing {@code
   *                   null}, {@link #RELEASE_PLACEHOLDER} or {@link #LATEST_PLACEHOLDER} and an
   *                   type field of {@code null}.
   * @return An {@link ArtifactCoordinate} with a version field that is not {@code null}, nor one of
   * the placeholder values, and a type field that is not {@code null}.
   */
  ArtifactCoordinate resolveCoordinate(ArtifactCoordinate coordinate);

  /**
   * Opens a stream for a file in this repository.
   *
   * @param coordinate Coordinate for the requested file.
   * @return An {@link InputStream} to the requested file.
   */
  InputStream openStream(ArtifactCoordinate coordinate) throws IOException;

  /**
   * Opens a stream for a file in this repository.
   *
   * @param pathComponents Path to the requested file.
   * @return An {@link InputStream} to the requested file.
   */
  InputStream openStream(String... pathComponents) throws IOException;

}
