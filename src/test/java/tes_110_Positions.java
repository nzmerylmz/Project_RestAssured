import Model.Positions;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class tes_110_Positions {
    Positions positions= new Positions();

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
    String positionID;
    String positionName;
    String positionShortName;
    String tenantId;
    boolean active;
    @Test
    public void createPosition(){
        positionName=getRandomName();
        positionShortName=getRandomShortName();
        Positions position= new Positions();
        position.setName(positionName);
        position.setShortName(positionShortName);
        position.setTenantId("5fe0786230cc4d59295712cf");
        position.setActive(true);

        positionID=
                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .log().body()
                        .body(position)
                        .when()
                        .post("school-sevice/api/employee-position")
                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().jsonPath().getString("id")
                ;
    }
    public String getRandomName() {
        return RandomStringUtils.randomAlphabetic(30).toLowerCase();
    }

    public String getRandomShortName() {
        return RandomStringUtils.randomAlphabetic(3).toLowerCase();
    }

    @Test(dependsOnMethods = "createPosition")
    public void createPositionNegative(){

        Positions position= new Positions();
        position.setName(positionName);
        position.setShortName(positionShortName);
        position.setTenantId("5fe0786230cc4d59295712cf");
        position.setActive(true);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(position)
                .when()
                .post("school-sevice/api/employee-position")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("The Position with Name \""+positionName+"\" already exists."))
        ;
    }
    @Test(dependsOnMethods = "createPosition")
    public void updatePosition() {

        positionName = getRandomName();

        Positions position = new Positions();
        position.setId(positionID);
        position.setName(positionName);
        position.setShortName(positionShortName);
        position.setTenantId("5fe0786230cc4d59295712cf");
        position.setActive(true);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(position)

                .when()
                .put("school-sevice/api/employee-position")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(positionName))
        ;
    }
    @Test(dependsOnMethods = "updatePosition")
    public void deletePositionById() {
        given()
                .cookies(cookies)
                .pathParam("positionID",positionID)
                .when()
                .delete("school-sevice/api/employee-position")
                .then()
                .log().body()
                .statusCode(204)
       ;
    }
    @Test(dependsOnMethods = "deletePositionById")
    public void deletePositionByIdNegative() {
        given()
                .cookies(cookies)
                .pathParam("positionID",positionID)
                .log().uri()

                .when()
                .delete("school-sevice/api/employee-position")
                .then()
                .log().body()
                .statusCode(400)
        ;
    }
    @Test(dependsOnMethods = "deletePositionById")
    public void updatePositionNegative() {

        positionName = getRandomName();

        Positions position = new Positions();
        position.setId(positionID);
        position.setName(positionName);
        position.setShortName(positionShortName);
        position.setTenantId("5fe0786230cc4d59295712cf");
        position.setActive(true);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(position)

                .when()
                .put("school-sevice/api/employee-position")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("Can't find Position"))
        ;
    }







}
