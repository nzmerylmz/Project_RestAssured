import Model.PositionCategories;
import Model.SubjectCategories;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class tes_106_PositionCategories {
    public String getRandomName() {
        return RandomStringUtils.randomAlphabetic(8);
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
    String positionCategoryID;

    @Test
    public void createPositionCategories() {
        name = getRandomName();
        PositionCategories pc = new PositionCategories();
        pc.setName(name);

        positionCategoryID =
                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(pc)
                        .when()
                        .post("school-service/api/position-category")
                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().jsonPath().getString("id");
    }

    @Test(dependsOnMethods = "createPositionCategories")
    public void createPositionCategoriesNegative() {
        PositionCategories pc = new PositionCategories();
        pc.setName(name);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(pc)

                .when()
                .post("school-service/api/position-category")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("The Position Category with Name \"" + name + "\" already exists."))
        ;
    }
    @Test(dependsOnMethods = "createPositionCategories")
    public void updatePositionCategories() {
        name=getRandomName();
        PositionCategories pc=new PositionCategories();
        pc.setId(positionCategoryID);
        pc.setName(name);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(pc)

                .when()
                .put("school-service/api/position-category")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(name))
        ;
    }
    @Test(dependsOnMethods = "updatePositionCategories")
    public void deletePositionCategoriesByID() {

        given()
                .cookies(cookies)
                .pathParam("positionCategoryID",positionCategoryID)

                .when()
                .delete("school-service/api/position-category/{positionCategoryID}")

                .then()
                .log().body()
                .statusCode(204);

    }
    @Test(dependsOnMethods = "deletePositionCategoriesByID")
    public void deletePositionCategoriesByIDNegative() {

        given()
                .cookies(cookies)
                .pathParam("positionCategoryID",positionCategoryID)

                .when()
                .delete("school-service/api/position-category/{positionCategoryID}")

                .then()
                .log().body()
                .statusCode(400);

    }
    @Test(dependsOnMethods = "deletePositionCategoriesByID")
    public void updatePositionCategoriesNegative() {
        name=getRandomName();
        PositionCategories pc=new PositionCategories();
        pc.setId(positionCategoryID);
        pc.setName(name);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(pc)

                .when()
                .put("school-service/api/position-category")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("Can't find Position Category"))
        ;
    }
}
