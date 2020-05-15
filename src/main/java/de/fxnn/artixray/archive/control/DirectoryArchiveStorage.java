package de.fxnn.artixray.archive.control;

import de.fxnn.artixray.archive.boundary.Archive;
import de.fxnn.artixray.archive.boundary.ArchiveReadException;
import de.fxnn.artixray.archive.boundary.ArchiveWriteException;
import de.fxnn.artixray.repository.boundary.ArtifactCoordinate;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class DirectoryArchiveStorage implements ArchiveStorage {

  private static final String USE_DEFAULT_STORAGE_DIR = "__ARTIXRAY__USE_DEFAULT_STORAGE_DIR__";
  private static final Path DEFAULT_STORAGE_DIR = Paths
      .get(System.getProperty("java.io.tmpdir"), "artixray");
  private static final String ARCHIVE_PREFIX = "artixray-archive-";
  private static final String ARCHIVE_SUFFIX = ".zip";

  private final ConcurrentHashMap<ArtifactCoordinate, Path> archivePathMap = new ConcurrentHashMap<>();

  @Inject
  @ConfigProperty(name = "artixray.archive.storage.dir", defaultValue = USE_DEFAULT_STORAGE_DIR)
  String storageDir;

  public Archive supplyArchive(ArtifactCoordinate coordinate,
      Function<ArtifactCoordinate, InputStream> downloadArchive) {
    Path path = archivePathMap.computeIfAbsent(coordinate, c -> storeArchive(c, downloadArchive));
    return new SuppliedInputStreamArchive(path, () -> openArchiveStream(path));
  }

  private Path storeArchive(ArtifactCoordinate coordinate,
      Function<ArtifactCoordinate, InputStream> downloadArchive) {
    try {
      Path path = createTemporaryArchiveFile();
      try (var os = Files
          .newOutputStream(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
        IOUtils.copy(downloadArchive.apply(coordinate), os);
      }
      return path;
    } catch (IOException e) {
      throw new ArchiveWriteException("Cannot store archive '" + coordinate + "'", e);
    }
  }

  private InputStream openArchiveStream(Path path) {
    try {
      return Files.newInputStream(path, StandardOpenOption.READ);
    } catch (IOException e) {
      throw new ArchiveReadException("Cannot open archive file '" + path + "' for reading", e);
    }
  }

  private Path createTemporaryArchiveFile() throws IOException {
    Path dir;
    if (USE_DEFAULT_STORAGE_DIR.equals(storageDir)) {
      dir = DEFAULT_STORAGE_DIR;
    } else {
      dir = Paths.get(storageDir);
    }
    Files.createDirectories(dir);
    return Files.createTempFile(dir, ARCHIVE_PREFIX, ARCHIVE_SUFFIX);
  }

}
