package de.fxnn.artixray.archive.control;

import de.fxnn.artixray.archive.boundary.Archive;
import de.fxnn.artixray.archive.boundary.ArchiveFile;
import de.fxnn.artixray.archive.boundary.ArchiveResolver;
import de.fxnn.artixray.repository.boundary.ArtifactCoordinate;
import de.fxnn.artixray.repository.boundary.Repository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;

@ApplicationScoped
public class DefaultArchiveResolver implements ArchiveResolver {

  @Inject
  Repository repository;

  @Override
  public Archive resolve(String coordinatesString) {
    var coordinate = ArtifactCoordinate.fromString(coordinatesString);
    var resolvedCoordinate = repository.resolveVersion(coordinate);
    return createDummyArchive(resolvedCoordinate);
  }

  private Archive createDummyArchive(ArtifactCoordinate coordinates) {
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
