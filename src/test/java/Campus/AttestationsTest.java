package Campus;
import Campus.Model.Attestations;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class AttestationsTest {
    Cookies cookies;

    @BeforeClass
    public void loginCampus() {
        baseURI = "https://demo.mersys.io/";

        Map<String, String> credential = new HashMap<>();
        credential.put("username", "richfield.edu");
        credential.put("password", "Richfield2020!");
        credential.put("rememberMe", "true");

        cookies =
                given()
                        .contentType(ContentType.JSON)
                        .body(credential)

                        .when()
                        .post("auth/login")

                        .then()
                        //.log().cookies()
                        .statusCode(200)
                        .extract().response().getDetailedCookies()
        ;
    }

    String attestationsID;
    String countryName;
    String countryCode;

    @Test
    public void createAttestations() {
        countryName = getRandomName();
        countryCode = getRandomCode();

        Attestations attestations = new Attestations();
        attestations.setName(countryName); // generateCountrName
        attestations.setCode(countryCode); // generateCountrCode

        attestationsID =
                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(attestations)

                        .when()
                        .post("school-service/api/attestation")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().jsonPath().getString("id")
        ;

    }

    public String getRandomName() {
        return RandomStringUtils.randomAlphabetic(8).toLowerCase();
    }

    public String getRandomCode() {
        return RandomStringUtils.randomAlphabetic(3).toLowerCase();
    }


    @Test(dependsOnMethods = "createAttestations")
    public void createAttestationsNegative() {
        //"message": "The Country with Name \"France 375\" already exists.",

        Attestations attestations = new Attestations();
        attestations.setName(countryName); // generateCountrName
        attestations.setCode(countryCode); // generateCountrCode


        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(attestations)

                .when()
                .post("school-service/api/attestation")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("The Attestation  with Name \""+countryName+"\" already exists."))
        ;
    }
    //The Attestation with Name "zpppısry" already exists.
    @Test(dependsOnMethods = "createAttestations")
    public void updateAttestations()
    {
        //"message": "The Country with Name \"France 375\" already exists.",
        countryName = getRandomName();

        Attestations attestations = new Attestations();
        attestations.setId(attestationsID);
        attestations.setName(countryName); // generateCountrName
        attestations.setCode(countryCode); // generateCountrCode

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(attestations)

                .when()
                .put("school-service/api/attestation")

                .then()
                .log().body()
                .statusCode(200)
                .body("name",equalTo(countryName))
        ;
    }

    @Test(dependsOnMethods = "updateAttestations")
    public void deleteAttestationsById()
    {
        given()
                .cookies(cookies)
                .pathParam("attestationsID", attestationsID)

                .when()
                .delete("school-service/api/attestation/{attestationsID}")

                .then()
                .log().body()
                .statusCode(204)
        ;
    }

    @Test(dependsOnMethods = "deleteAttestationsById")
    public void deleteAttestationsByIdNegative()
    {
        given()
                .cookies(cookies)
                .pathParam("attestationsID", attestationsID)
                .log().uri()
                .when()
                .delete("school-service/api/attestation/{attestationsID}")

                .then()
                .log().body()
                .statusCode(400)
        ;
    }

    @Test(dependsOnMethods = "deleteAttestationsById")
    public void updateAttestationsNegative()
    {
        countryName = getRandomName();

        Attestations attestations = new Attestations();
        attestations.setId(attestationsID);
        attestations.setName(countryName); // generateCountrName
        attestations.setCode(countryCode); // generateCountrCode

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(attestations)

                .when()
                .put("school-service/api/attestation")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("Can't find Attestation"))
        ;

    }





}