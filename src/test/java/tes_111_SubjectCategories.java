import Model.SubjectCategories;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class tes_111_SubjectCategories {
    public String getRandomName() {
        return RandomStringUtils.randomAlphabetic(8);
    }

    public String getRandomCode() {
        return RandomStringUtils.randomAlphanumeric(3);
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
    String code;
    String subjectCategoryID;

    @Test
    public void createSubjectCategories() {
        name = getRandomName();
        code = getRandomCode();
        SubjectCategories sc = new SubjectCategories();
        sc.setName(name);
        sc.setCode(code);
        sc.setActive(true);
        // sc.getTranslateName();

        subjectCategoryID =
                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(sc)
                        .when()
                        .post("school-service/api/subject-categories")
                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().jsonPath().getString("id");
    }

    @Test(dependsOnMethods = "createSubjectCategories")
    public void createSubjectCategoriesNegative() {
        SubjectCategories sc = new SubjectCategories();
        sc.setName(name);
        sc.setCode(code);
        sc.setActive(true);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(sc)

                .when()
                .post("school-service/api/subject-categories")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("The Subject Category with Name \"" + name + "\" already exists."))
        ;
    }

    @Test(dependsOnMethods = "createSubjectCategories")
    public void updateSubjectCategories() {
        name=getRandomName();
        SubjectCategories sc = new SubjectCategories();
        sc.setId(subjectCategoryID);
        sc.setName(name);
        sc.setCode(code);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(sc)

                .when()
                .put("school-service/api/subject-categories")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(name))
        ;
    }
    @Test(dependsOnMethods = "updateSubjectCategories")
    public void deleteSubjectCategoryById()
    {
        given()
                .cookies(cookies)
                .pathParam("subjectCategoryID", subjectCategoryID)

                .when()
                .delete("school-service/api/subject-categories/{subjectCategoryID}")

                .then()
                .log().body()
                .statusCode(200)
        ;
    }
    @Test(dependsOnMethods = "deleteSubjectCategoryById")
    public void deleteSubjectCategoryByIdNegative()
    {
        given()
                .cookies(cookies)
                .pathParam("subjectCategoryID", subjectCategoryID)

                .when()
                .delete("school-service/api/subject-categories/{subjectCategoryID}")

                .then()
                .log().body()
                .statusCode(400)
        ;
    }

    @Test(dependsOnMethods = "deleteSubjectCategoryById")
    public void updateSubjectCategoriesNegative() {
        name=getRandomName();
        SubjectCategories sc = new SubjectCategories();
        sc.setId(subjectCategoryID);
        sc.setName(name);
        sc.setCode(code);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(sc)

                .when()
                .put("school-service/api/subject-categories")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("Can't find Subject Category"))
        ;
    }
}
