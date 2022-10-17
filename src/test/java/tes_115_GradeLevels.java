import Model.GradeLevels;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

public class tes_115_GradeLevels {
    public String getRandomName() {
        return RandomStringUtils.randomAlphabetic(8);
    }

    public String getRandomShortName() {
        return RandomStringUtils.randomAlphabetic(4);
    }

    public String getRandomOrder() {
        return RandomStringUtils.randomNumeric(4);
    }

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
                        .statusCode(200)
                        .extract().response().getDetailedCookies()
        ;
    }

    String name;
    String shortName;
    String gradeLevelsID;
    String order;

    @Test
    public void createGradeLevels() {
        name = getRandomName();
        shortName = getRandomShortName();
        order = getRandomOrder();
        GradeLevels gl = new GradeLevels();
        gl.setName(name);
        gl.setShortName(shortName);
        gl.setOrder(order);
        gl.setActive(true);

        gradeLevelsID =
                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(gl)
                        .when()
                        .post("school-service/api/grade-levels")
                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().jsonPath().getString("id");
    }

    @Test(dependsOnMethods = "createGradeLevels")
    public void createGradeLevelsNegative() {
        GradeLevels gl = new GradeLevels();
        gl.setName(name);
        gl.setShortName(shortName);
        gl.setOrder(order);
        gl.setActive(true);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(gl)
                .when()
                .post("school-service/api/grade-levels")
                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("The Grade Level with Name \"" + name + "\" already exists."));
    }
    @Test(dependsOnMethods = "createGradeLevels")
    public void updateGradeLevels() {
        name=getRandomName();
        GradeLevels gl = new GradeLevels();
        gl.setName(name);
        gl.setId(gradeLevelsID);
        gl.setShortName(shortName);
        gl.setOrder(order);
        gl.setActive(true);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(gl)
                .when()
                .put("school-service/api/grade-levels")
                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(name));
    }
    @Test(dependsOnMethods = "updateGradeLevels")
    public void deleteGradeLevelsById() {

        given()
                .cookies(cookies)
                .pathParam("gradeLevelsID",gradeLevelsID)
                .when()
                .delete("school-service/api/grade-levels/{gradeLevelsID}")
                .then()
                .log().body()
                .statusCode(200);
    }
    @Test(dependsOnMethods = "deleteGradeLevelsById")
    public void deleteGradeLevelsByIdNegative() {

        given()
                .cookies(cookies)
                .pathParam("gradeLevelsID",gradeLevelsID)
                .when()
                .delete("school-service/api/grade-levels/{gradeLevelsID}")
                .then()
                .log().body()
                .statusCode(400);
    }
    @Test(dependsOnMethods = "deleteGradeLevelsById")
    public void updateGradeLevelsNegative() {
        name=getRandomName();
        GradeLevels gl = new GradeLevels();
        gl.setName(name);
        gl.setId(gradeLevelsID);
        gl.setShortName(shortName);
        gl.setOrder(order);
        gl.setActive(true);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(gl)
                .when()
                .put("school-service/api/grade-levels")
                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("Grade Level not found."));
    }

}
