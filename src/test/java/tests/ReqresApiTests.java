package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;


public class ReqresApiTests extends TestBase{
    @Test
    void successfulRegisterTest() {
        Response statusResponse = given()
                .log().uri()
                .log().method()
                .contentType(JSON)
                .log().body()
                .body("{ \"email\": \"eve.holt@reqres.in\", \"password\": \"pistol\" }")
                .when()
                .post("/register")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/successRegisterSchema.json"))
                .extract().response();

        assertThat(statusResponse.path("id"), is(4));
        assertThat(statusResponse.path("token"), is("QpwL5tke4Pnpja7X4"));
    }
    @Test
    void NegativeNoContentTypeTest() {
        Response statusResponse = given()
                .log().uri()
                .log().method()
                .log().body()
                .body("{ \"email\": \"eve.holt@reqres.in\", \"password\": \"pistol\" }")
                .when()
                .post("/register");
        statusResponse.then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body("error", is("Missing email or username"));
    }
    @Test
    void NegativeEmailTest() {
        Response statusResponse = given()
                .log().uri()
                .log().method()
                .contentType(JSON)
                .log().body()
                .body("{ \"email\": \"@reqres.in\", \"password\": \"pistol\" }")
                .when()
                .post("/register");
        statusResponse.then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body("error", is("Note: Only defined users succeed registration"));
    }
    @Test
    void checkTotalWithJsonSchema() {
        Response statusResponse = given()
                .log().uri()
                .log().method()
                .when()
                .get("/users?page=2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/usersResponseSchema.json"))
                .extract().response();

        assertThat(statusResponse.path("total"), is(12));
    }
    @Test
    void putUserPositiveTest() {
        Response statusResponse = given()
                .log().uri()
                .log().method()
                .contentType(JSON)
                .log().body()
                .body("{ \"name\": \"morpheus\", \"job\": \"zion resident\" }")
                .when()
                .put("/users/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/putUserResponseSchema.json"))
                .extract().response();

        assertThat(statusResponse.path("name"), is("morpheus"));
        assertThat(statusResponse.path("job"), is("zion resident"));
        assertThat(statusResponse.path("updatedAt"), notNullValue());
    }
}
