package de.fxnn.artixray.bootstrap.boundary;

import de.fxnn.artixray.archive.boundary.Archive;
import de.fxnn.artixray.archive.boundary.ArchiveFile;
import de.fxnn.artixray.archive.boundary.ArchiveResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/artifact")
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    @Inject
    ArchiveResolver archiveResolver;

    @GET
    @Path("{coordinates}/file/{filePath : .+}")
    public Response resolveFileFromArtifact(@PathParam("coordinates") String coordinates,
        @PathParam("filePath") String filePath) {
        LOG.debug("For artifact '{}', resolving '{}'", coordinates, filePath);
        Archive archive = archiveResolver.resolve(coordinates);
        ArchiveFile file = archive.readFile(filePath);
        return Response
            .ok(file.getInputStream(), file.getContentType())
            .build();
    }
}