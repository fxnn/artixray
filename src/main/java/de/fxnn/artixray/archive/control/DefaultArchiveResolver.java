package de.fxnn.artixray.archive.control;

import de.fxnn.artixray.archive.boundary.Archive;
import de.fxnn.artixray.archive.boundary.ArchiveResolver;
import de.fxnn.artixray.archive.boundary.ArchiveWriteException;
import de.fxnn.artixray.repository.boundary.ArtifactCoordinate;
import de.fxnn.artixray.repository.boundary.Repository;
import java.io.IOException;
import java.io.InputStream;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DefaultArchiveResolver implements ArchiveResolver {

  @Inject
  Repository repository;

  @Inject
  ThreadSafeDirectoryArchiveStorage archiveStorage;

  @Override
  public Archive resolve(String coordinatesString) {
    var unresolvedCoordinate = ArtifactCoordinate.fromString(coordinatesString);
    var coordinate = repository.resolveCoordinate(unresolvedCoordinate);
    return archiveStorage.supplyArchive(coordinate, this::downloadArchive);
  }

  private InputStream downloadArchive(ArtifactCoordinate coordinate) {
    try {
      return repository.openStream(coordinate);
    } catch (IOException e) {
      throw new ArchiveWriteException(
          "Failed to retrieve artifact '" + coordinate + "' from repository '" + repository + "'",
          e);
    }
  }

}
