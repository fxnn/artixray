package de.fxnn.artixray.archive.boundary;

import java.io.InputStream;
import java.io.OutputStream;

public class ArchiveFile {

  private final String contentType;
  private final InputStream inputStream;

  public ArchiveFile(String contentType, InputStream inputStream) {
    this.contentType = contentType;
    this.inputStream = inputStream;
  }

  public String getContentType() {
    return contentType;
  }

  public InputStream getInputStream() {
    return inputStream;
  }
}
