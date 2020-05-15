package de.fxnn.artixray.archive.boundary;

import java.io.InputStream;
import java.util.function.Supplier;

public class ArchiveFile {

  private final String contentType;
  private final Supplier<InputStream> inputStreamSupplier;

  public ArchiveFile(String contentType, Supplier<InputStream> inputStreamSupplier) {
    this.contentType = contentType;
    this.inputStreamSupplier = inputStreamSupplier;
  }

  public String getContentType() {
    return contentType;
  }

  public InputStream openInputStream() {
    return inputStreamSupplier.get();
  }
}
