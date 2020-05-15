package de.fxnn.artixray.bootstrap.boundary;

import com.google.common.base.Throwables;
import de.fxnn.artixray.archive.boundary.Archive;
import de.fxnn.artixray.archive.boundary.ArchiveFile;
import de.fxnn.artixray.archive.boundary.ArchiveResolver;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/artifact")
public class Application {

  private static final Logger LOG = LoggerFactory.getLogger(Application.class);

  @Inject
  ArchiveResolver archiveResolver;

  @GET
  @Path("{coordinates}/file/{filePath : .+}")
  public Response resolveFileFromArtifact(@PathParam("coordinates") String coordinates,
      @PathParam("filePath") String filePath) {
    try {
      LOG.debug("For artifact '{}', resolving '{}'", coordinates, filePath);
      Archive archive = archiveResolver.resolve(coordinates);
      ArchiveFile file = archive.readFile(filePath);
      return Response.ok(file.openInputStream(), file.getContentType()).build();
    } catch (Exception ex) {
      String stackTrace = Throwables.getStackTraceAsString(ex);
      return Response.serverError()
          .type(MediaType.TEXT_PLAIN_TYPE)
          .entity(String.format("Failed to deliver file\n\t%s\nfor artifact\n\t%s\n\n%s", filePath,
              coordinates, stackTrace))
          .build();
    }
  }
}