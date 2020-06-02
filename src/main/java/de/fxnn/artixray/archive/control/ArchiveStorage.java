package de.fxnn.artixray.archive.control;

import de.fxnn.artixray.archive.boundary.Archive;
import de.fxnn.artixray.repository.boundary.ArtifactCoordinate;
import java.io.InputStream;
import java.util.function.Function;

public interface ArchiveStorage {

  void initializeStorage();

  Archive supplyArchive(ArtifactCoordinate coordinate, Function<ArtifactCoordinate, InputStream> downloadArchive);
}
