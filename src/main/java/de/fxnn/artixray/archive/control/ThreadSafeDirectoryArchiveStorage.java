package de.fxnn.artixray.archive.control;

import de.fxnn.artixray.archive.boundary.Archive;
import de.fxnn.artixray.archive.boundary.ArchiveReadException;
import de.fxnn.artixray.archive.boundary.ArchiveWriteException;
import de.fxnn.artixray.repository.boundary.ArtifactCoordinate;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ThreadSafeDirectoryArchiveStorage implements ArchiveStorage {

  private final ExecutorService executor = Executors.newSingleThreadExecutor();

  @Inject
  DirectoryArchiveStorage wrapped;

  @Override
  public Archive supplyArchive(ArtifactCoordinate coordinate, Function<ArtifactCoordinate, InputStream> downloadArchive) {
    try {
      return executor
          .submit(() -> wrapped.supplyArchive(coordinate, downloadArchive))
          .get();
    } catch (InterruptedException e) {
      throw new IllegalStateException("Interrupted unexpectedly while opening archive '" + coordinate+ "'", e);
    } catch (ExecutionException e) {
      if (e.getCause() instanceof ArchiveWriteException) {
        throw new ArchiveWriteException("Failed to download archive '" + coordinate + "'", e);
      }
      if (e.getCause() instanceof ArchiveReadException) {
        throw new ArchiveReadException("Failed to store/retrieve archive '" + coordinate + "'", e);
      }
      throw new IllegalStateException("Asynchronous storage operations failed for archive '" + coordinate + "'", e);
    }
  }

}
