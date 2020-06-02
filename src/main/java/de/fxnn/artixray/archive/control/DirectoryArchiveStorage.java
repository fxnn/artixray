package de.fxnn.artixray.archive.control;

import de.fxnn.artixray.archive.boundary.Archive;
import de.fxnn.artixray.archive.boundary.ArchiveReadException;
import de.fxnn.artixray.archive.boundary.ArchiveWriteException;
import de.fxnn.artixray.repository.boundary.ArtifactCoordinate;
import de.fxnn.artixray.util.boundary.AccessOrderedLinkedHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores archive in a local directory.
 */
@ApplicationScoped
public class DirectoryArchiveStorage implements ArchiveStorage {

  private static final String USE_DEFAULT_STORAGE_DIR = "__ARTIXRAY__USE_DEFAULT_STORAGE_DIR__";
  private static final Path DEFAULT_STORAGE_DIR = Paths
      .get(System.getProperty("java.io.tmpdir"), "artixray");
  private static final String DEFAULT_SIZE_LIMIT = "209715200"; // 2 MiB

  private static final String ARCHIVE_PREFIX = "artixray-archive-";
  private static final String ARCHIVE_SUFFIX = ".zip";

  private static final Logger LOG = LoggerFactory.getLogger(DirectoryArchiveStorage.class);

  private final Map<ArtifactCoordinate, StoredArchive> storedArchiveMap = new AccessOrderedLinkedHashMap<>();

  @Inject
  @ConfigProperty(name = "artixray.archive.storage.dir", defaultValue = USE_DEFAULT_STORAGE_DIR)
  String storageDir;

  @Inject
  @ConfigProperty(name = "artixray.archive.storage.dir.size.limit", defaultValue = DEFAULT_SIZE_LIMIT)
  long sizeLimit;

  @Override
  public void initializeStorage() {
    try (DirectoryStream<Path> files = Files
        .newDirectoryStream(getStorageDir(), ARCHIVE_PREFIX + "*" + ARCHIVE_SUFFIX)) {
      long sizeDeleted = 0;
      long numberDeleted = 0;
      for (Path file : files) {
        try {
          sizeDeleted += Files.size(file);
          Files.delete(file);
          numberDeleted++;
        } catch (IOException ex) {
          LOG.warn("Failed to delete existing storage dir file '" + file + "'", ex);
        }
      }
      LOG.info("Deleted {} bytes from {} pre-existing files in storage dir '{}'", sizeDeleted,
          numberDeleted, getStorageDir());
    } catch (IOException ex) {
      LOG.warn("Failed to read files in storage dir '" + getStorageDir() + "'", ex);
    }
  }

  public Archive supplyArchive(ArtifactCoordinate coordinate,
      Function<ArtifactCoordinate, InputStream> downloadArchive) {
    StoredArchive storedArchive = storedArchiveMap.get(coordinate);
    if (storedArchive == null) {
      storedArchive = storeArchive(coordinate, downloadArchive);
      storedArchiveMap.put(coordinate, storedArchive);
    }

    Path path = storedArchive.getPath();
    return new SuppliedInputStreamArchive(path, () -> openArchiveStream(path));
  }

  private StoredArchive storeArchive(ArtifactCoordinate coordinate,
      Function<ArtifactCoordinate, InputStream> downloadArchive) {
    try {
      long size;
      Path path = createTemporaryArchiveFile();
      try (var os = Files
          .newOutputStream(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
        size = IOUtils.copy(downloadArchive.apply(coordinate), os);
      }
      LOG.trace("Stored {} archive bytes for {} at path {}", size, coordinate, path);
      // NOTE, that at this moment, we may exceed the size limit (already stored on disk, but not
      //   yet cleaned up).
      ensureSizeLimitMet(size);
      return new StoredArchive(path, size);
    } catch (IOException e) {
      throw new ArchiveWriteException("Cannot store archive '" + coordinate + "'", e);
    }
  }

  private void ensureSizeLimitMet(long sizeToAdd) {
    // NOTE, that we handle the archive to add separately (in parameter sizeToAdd).
    //   An alternative would be to ensure the size limit _after_ adding the file, which however
    //   would need additional attention not to delete the file just added.
    long sizeTotal = storedArchiveMap.values().stream()
        .mapToLong(StoredArchive::getSize)
        .sum() + sizeToAdd;
    long sizeDeleted = 0L;
    long numberDeleted = 0L;

    Iterator<StoredArchive> leastRecentlyUsedIterator = storedArchiveMap.values().iterator();
    while (sizeTotal > sizeLimit && leastRecentlyUsedIterator.hasNext()) {
      StoredArchive archive = leastRecentlyUsedIterator.next();
      sizeTotal -= archive.getSize();
      leastRecentlyUsedIterator.remove();

      try {
        Files.delete(archive.getPath());
        sizeDeleted += archive.getSize();
        numberDeleted++;
        LOG.trace("Deleted {} archive bytes at path '{}'", archive.getSize(), archive.getPath());
      } catch (IOException ex) {
        LOG.warn("Failed to delete '{}' in order to ensure size limit", archive.getPath(), ex);
      }
    }

    if (numberDeleted > 0) {
      LOG.debug("Deleted {} archive bytes in {} files", sizeDeleted, numberDeleted);
    }
    // NOTE, that we will still exceed the size limit when sizeToAdd > sizeTotal
  }

  private InputStream openArchiveStream(Path path) {
    try {
      return Files.newInputStream(path, StandardOpenOption.READ);
    } catch (IOException e) {
      throw new ArchiveReadException("Cannot open archive file '" + path + "' for reading", e);
    }
  }

  private Path createTemporaryArchiveFile() throws IOException {
    Path dir = getStorageDir();
    Files.createDirectories(dir);
    return Files.createTempFile(dir, ARCHIVE_PREFIX, ARCHIVE_SUFFIX);
  }

  private Path getStorageDir() {
    Path dir;
    if (USE_DEFAULT_STORAGE_DIR.equals(storageDir)) {
      dir = DEFAULT_STORAGE_DIR;
    } else {
      dir = Paths.get(storageDir);
    }
    return dir;
  }

  private static class StoredArchive {

    private final long size;
    private final Path path;

    public StoredArchive(Path path, long size) {
      this.size = size;
      this.path = path;
    }

    public long getSize() {
      return size;
    }

    public Path getPath() {
      return path;
    }
  }

}
