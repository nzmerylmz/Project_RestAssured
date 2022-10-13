import Model.Fields;



import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
public class test_109_Fields {

    public String getRandomName() {
        return RandomStringUtils.randomAlphabetic(8);
    }

    public String getRandomCode() {
        return RandomStringUtils.randomAlphanumeric(5);
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

    String fieldsName;

    String fieldsCode;

    String fieldsID;

    String fieldsSchoolId;

    @Test
    public void createFields() {

        fieldsName = getRandomName();
        fieldsCode = getRandomCode();
        fieldsSchoolId = "5fe07e4fb064ca29931236a5";
        Fields fields = new Fields();
        fields.setName(fieldsName);
        fields.setCode(fieldsCode);
        fields.setType("STRING");
        fields.setSchoolId(fieldsSchoolId);

        fieldsID =
                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(fields)


                        .when()
                        .post("school-service/api/entity-field")


                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().jsonPath().getString("id");


    }

    @Test(dependsOnMethods = "createFields")
    public void createFieldsNegative() {

        Fields fields = new Fields();
        fields.setName(fieldsName);
        fields.setCode(fieldsCode);
        fields.setType("STRING");
        fields.setSchoolId(fieldsSchoolId);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(fields)

                .when()
                .post("school-service/api/entity-field")
                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("The SchoolMessages.EntityField.Title with Code \"" + fieldsCode + "\" already exists."));


    }

    @Test(dependsOnMethods = "createFields")
    public void updateFields() {
        fieldsName = getRandomName();


        Fields fields = new Fields();
        fields.setId(fieldsID);
        fields.setName(fieldsName);
        fields.setCode(fieldsCode);
        fields.setType("STRING");
        fields.setSchoolId(fieldsSchoolId);

        given()

                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(fields)

                .when()
                .put("school-service/api/entity-field")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(fieldsName));


    }

    @Test(dependsOnMethods = "updateFields")
    public void deleteFieldsById() {
        given()
                .cookies(cookies)
                .pathParam("fieldsID", fieldsID)

                .when()
                .delete("school-service/api/entity-field/{fieldsID}")

                .then()
                .log().body()
                .statusCode(204);


    }

    @Test(dependsOnMethods = "deleteFieldsById")
    public void deleteFieldsByIdNegative() {
        given()
                .cookies(cookies)
                .pathParam("fieldsID", fieldsID)

                .when()
                .delete("school-service/api/entity-field/{fieldsID}")

                .then()
                .log().body()
                .statusCode(400);

    }

    @Test(dependsOnMethods = "deleteFieldsById")
    public void updateFieldsNegative() {
        fieldsName = getRandomName();


        Fields fields = new Fields();
        fields.setId(fieldsID);
        fields.setName(fieldsName);
        fields.setCode(fieldsCode);
        fields.setType("STRING");
        fields.setSchoolId(fieldsSchoolId);

        given()

                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(fields)

                .when()
                .put("school-service/api/entity-field")

                .then()
                .log().body()
                .statusCode(400)

                .body("message", equalTo("EntityField not found"));


    }
}
