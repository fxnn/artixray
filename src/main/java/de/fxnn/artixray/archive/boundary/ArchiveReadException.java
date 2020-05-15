package de.fxnn.artixray.archive.boundary;

public class ArchiveReadException extends RuntimeException {

  public ArchiveReadException(String message, Throwable cause) {
    super(message, cause);
  }

  public ArchiveReadException(String message) {
    super(message);
  }
}
