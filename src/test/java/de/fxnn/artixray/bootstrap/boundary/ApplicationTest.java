package de.fxnn.artixray.bootstrap.boundary;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class ApplicationTest {

    @Test
    public void artifactFileEndpoint() {
        String coordinates = "groupId:artifactId:version";
        String filePath = "path/to/file";

        given()
          .when().get("/artifact/{coordinates}/file/{filePath}", coordinates, filePath)
          .then()
             .statusCode(200)
             .body(is(coordinates + "\n" + filePath + "\n"));
    }

}