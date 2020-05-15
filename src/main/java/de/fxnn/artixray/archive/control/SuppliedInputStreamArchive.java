package de.fxnn.artixray.archive.control;

import de.fxnn.artixray.archive.boundary.Archive;
import de.fxnn.artixray.archive.boundary.ArchiveFile;
import de.fxnn.artixray.archive.boundary.ArchiveReadException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuppliedInputStreamArchive implements Archive {

  private static final Logger LOG = LoggerFactory.getLogger(SuppliedInputStreamArchive.class);

  private final Path archivePath;
  private final Supplier<InputStream> inputStreamSupplier;

  public SuppliedInputStreamArchive(Path archivePath, Supplier<InputStream> inputStreamSupplier) {
    this.archivePath = archivePath;
    this.inputStreamSupplier = inputStreamSupplier;
  }

  @Override
  public ArchiveFile readFile(String entryPath) {
    return new ArchiveFile(guessContentType(entryPath), () -> openFile(entryPath));
  }

  private String guessContentType(String entryPath) {
    try {
      return Files.probeContentType(Paths.get(entryPath));
    } catch (IOException e) {
      LOG.debug("Content Type guessing failed on '" + entryPath + "'", e);
    }

    return "binary/octet-stream";
  }

  private InputStream openFile(String entryPath) {
    ZipInputStream zis = null;
    try {
      zis = new ZipInputStream(openArchive());
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        if (Objects.equals(entryPath, entry.getName())) {
          return zis;
        }
      }
      throw new ArchiveReadException(
          "Archive '" + archivePath + "' contains no entry matching '" + entryPath + "'");
    } catch (IOException e) {
      try {
        zis.close();
      } catch (IOException f) {
        LOG.debug("Couldn't close stream '" + zis + "' for archive '" + archivePath
            + "' during handling of exception '" + e + "'", f);
      }
      throw new ArchiveReadException("Failed to read archive '" + archivePath + "'", e);
    }
  }

  private InputStream openArchive() {
    return inputStreamSupplier.get();
  }

  @Override
  public String toString() {
    return super.toString() + "{" + "archivePath=" + archivePath + '}';
  }
}
