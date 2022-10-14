import Model.Nationality;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class tes_117_Nationality {

    Cookies cookies;

    @BeforeClass
    public void loginCampus() {
        baseURI = "https://demo.mersys.io/";

        Map<String, String> credential = new HashMap<>();
        credential.put("username", "richfield.edu");
        credential.put("password", "Richfield2020!");
        credential.put("rememberMe", "true");

        cookies=
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

    String nationalityID;
    String countryName;
    String countryCode;

    @Test
    public void createNationality()
    {
        countryName=getRandomName();
        countryCode=getRandomCode();

        Nationality nationality=new Nationality();
        nationality.setName(countryName); // generateCountrName
        nationality.setCode(countryCode); // generateCountrCode

        nationalityID=
                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(nationality)

                        .when()
                        .post("school-service/api/nationality")

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

    @Test(dependsOnMethods ="createNationality")
    public void createNationalityNegative()
    {

        Nationality nationality=new Nationality();
        nationality.setName(countryName); // generateCountrName
        nationality.setCode(countryCode); // generateCountrCode


                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(nationality)

                        .when()
                        .post("school-service/api/nationality")

                        .then()
                        .log().body()
                        .statusCode(400)
                        .body("message",equalTo("The Nationality with Name \""+countryName+"\" already exists."))
        ;
    }

    @Test(dependsOnMethods ="createNationality")
    public void updateNationality()
    {
        countryName = getRandomName();

        Nationality nationality=new Nationality();
        nationality.setId(nationalityID);
        nationality.setName(countryName); // generateCountrName
        nationality.setCode(countryCode); // generateCountrCode


        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(nationality)

                .when()
                .put("school-service/api/nationality")

                .then()
                .log().body()
                .statusCode(200)
                .body("name",equalTo(countryName))
        ;
    }

    @Test(dependsOnMethods ="updateNationality")
    public void deleteNationality()
    {
        given()
                .cookies(cookies)
                .pathParam("nationalityID", nationalityID)

                .when()
                .delete("school-service/api/nationality/{nationalityID")

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test(dependsOnMethods ="deleteNationality")
    public void deleteNationalityNegative()
    {
        given()
                .cookies(cookies)
                .pathParam("nationalityID", nationalityID)
                .log().uri()
                .when()
                .delete("school-service/api/nationality/{nationalityID")

                .then()
                .log().body()
                .statusCode(400)
        ;
    }

    @Test(dependsOnMethods ="deleteNationality")
    public void updateNationalityNegative()
    {
        countryName = getRandomName();

        Nationality nationality=new Nationality();
        nationality.setId(nationalityID);
        nationality.setName(countryName); // generateCountrName
        nationality.setCode(countryCode); // generateCountrCode


        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(nationality)

                .when()
                .put("school-service/api/nationality")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("Can't find Nationality"))
        ;
    }






}
