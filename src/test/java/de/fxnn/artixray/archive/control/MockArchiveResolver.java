package de.fxnn.artixray.archive.control;

import de.fxnn.artixray.archive.boundary.Archive;
import de.fxnn.artixray.archive.boundary.ArchiveFile;
import de.fxnn.artixray.archive.boundary.ArchiveResolver;
import io.quarkus.test.Mock;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;

@Mock
@ApplicationScoped
public class MockArchiveResolver implements ArchiveResolver {

  @Override
  public Archive resolve(String coordinate) {
    return new Archive() {

      @Override
      public ArchiveFile readFile(String filePath) {
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(coordinate).append("\n");
        resultBuilder.append(filePath).append("\n");

        byte[] resultBytes = resultBuilder.toString().getBytes();
        ByteArrayInputStream resultStream = new ByteArrayInputStream(resultBytes);
        return new ArchiveFile(MediaType.TEXT_PLAIN, resultStream);
      }
    };
  }
}
