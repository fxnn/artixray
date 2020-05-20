package de.fxnn.artixray.bootstrap.boundary;

import com.google.common.base.Throwables;
import de.fxnn.artixray.archive.boundary.Archive;
import de.fxnn.artixray.archive.boundary.ArchiveFile;
import de.fxnn.artixray.archive.boundary.ArchiveResolver;
import de.fxnn.artixray.repository.boundary.ArtifactCoordinate;
import java.net.URI;
import java.net.URISyntaxException;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class Application {

  private static final Logger LOG = LoggerFactory.getLogger(Application.class);

  @Inject
  ArchiveResolver archiveResolver;

  @Inject
  BuildInfo buildInfo;

  @GET
  @Path("build")
  @Produces(MediaType.APPLICATION_JSON)
  public BuildInfo getBuildInfo() {
    return buildInfo;
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("artifact/coordinateExample")
  public String getArtifactCoordinateExample() {
    return ArtifactCoordinate.COORDINATE_EXAMPLE;
  }

  @GET
  @Path("artifact/{coordinates}/index")
  public Response redirectToArtifactIndex(@PathParam("coordinates") String coordinates) {
    try {
      // TODO: actually scan for suitable index files
      return Response
          .temporaryRedirect(new URI("/artifact/" + coordinates + "/file/index.html"))
          .build();
    } catch (URISyntaxException e) {
      throw new IllegalStateException("Could not create redirect URI for coordinates '" + coordinates + "'", e);
    }
  }

  @GET
  @Path("artifact/{coordinates}/file/{filePath : .+}")
  public Response resolveArtifactFile(@PathParam("coordinates") String coordinates,
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