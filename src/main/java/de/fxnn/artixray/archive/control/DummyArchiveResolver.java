package de.fxnn.artixray.archive.control;

import de.fxnn.artixray.archive.boundary.Archive;
import de.fxnn.artixray.archive.boundary.ArchiveFile;
import de.fxnn.artixray.archive.boundary.ArchiveResolver;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;

@ApplicationScoped
public class DummyArchiveResolver implements ArchiveResolver {

  @Override
  public Archive resolve(String coordinates) {
    return new Archive() {

      @Override
      public ArchiveFile readFile(String filePath) {
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(coordinates).append("\n");
        resultBuilder.append(filePath).append("\n");

        byte[] resultBytes = resultBuilder.toString().getBytes();
        ByteArrayInputStream resultStream = new ByteArrayInputStream(resultBytes);
        return new ArchiveFile(MediaType.TEXT_PLAIN, resultStream);
      }
    };
  }
}
