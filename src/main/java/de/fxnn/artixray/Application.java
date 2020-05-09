package de.fxnn.artixray;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/artifact")
public class Application {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }

    @GET
    @Path("{coordinates}/{filePath : .+}")
    public Response resolveFileFromArtifact(@PathParam("coordinates") String coordinates,
        @PathParam("filePath") String filePath) {
        return Response.ok(
            new StringBuilder()
            .append(coordinates).append("\n")
            .append(filePath).append("\n")
            .toString(),
            MediaType.TEXT_PLAIN_TYPE
        ).build();
    }
}