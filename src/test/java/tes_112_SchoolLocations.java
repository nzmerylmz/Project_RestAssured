import Model.DocumentTypes;
import Model.School;
import Model.SchoolLocations;
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

public class tes_112_SchoolLocations {
    public String getRandomName() {
        return RandomStringUtils.randomAlphabetic(8);
    }
    public String getRandomShortName() {
        return RandomStringUtils.randomAlphabetic(5);
    }
    public String getRandomCapacity() {
        return RandomStringUtils.randomNumeric(2);
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
    String capacity;
    String schoolLocationsID;
    @Test
    public void createDocumentType() {
        name = getRandomName();
        shortName=getRandomShortName();
        capacity=getRandomCapacity();
        SchoolLocations sl=new SchoolLocations();
        sl.setName(name);
        sl.setShortName(shortName);
        sl.setCapacity(capacity);
        sl.school=new School();
        sl.school.id="6343bf893ed01f0dc03a509a";
        sl.setType("CLASS");

        schoolLocationsID =
                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(sl)
                        .when()
                        .post("school-service/api/location")
                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().jsonPath().getString("id");
    }
    @Test(dependsOnMethods = "createDocumentType")
    public void createDocumentTypeNegative() {
        SchoolLocations sl=new SchoolLocations();
        sl.setName(name);
        sl.setShortName(shortName);
        sl.setCapacity(capacity);
        sl.school=new School();
        sl.school.id="6343bf893ed01f0dc03a509a";
        sl.setType("CLASS");

                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(sl)
                        .when()
                        .post("school-service/api/location")
                        .then()
                        .log().body()
                        .statusCode(400)
                        .body("message",equalTo("The School Location with Name \""+name+"\" already exists."));
    }

    @Test(dependsOnMethods ="createDocumentType" )
    public void updateDocumentType() {
        name = getRandomName();
        SchoolLocations sl=new SchoolLocations();
        sl.setName(name);
        sl.setShortName(shortName);
        sl.setCapacity(capacity);
        sl.school=new School();
        sl.school.id="6343bf893ed01f0dc03a509a";
        sl.setType("CLASS");
        sl.setId(schoolLocationsID);

                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(sl)
                        .when()
                        .put("school-service/api/location")
                        .then()
                        .log().body()
                        .statusCode(200)
                        .body("name",equalTo(name));
    }

    @Test(dependsOnMethods = "updateDocumentType")
    public void deleteDocumentTypeById() {

        given()
                .cookies(cookies)
                .pathParam("schoolLocationId",schoolLocationsID)
                .when()
                .delete("school-service/api/location/{schoolLocationId}")
                .then()
                .log().body()
                .statusCode(200);
    }
    @Test(dependsOnMethods = "deleteDocumentTypeById")
    public void deleteDocumentTypeByIdNegative() {

        given()
                .cookies(cookies)
                .pathParam("schoolLocationId",schoolLocationsID)
                .when()
                .delete("school-service/api/location/{schoolLocationId}")
                .then()
                .log().body()
                .statusCode(400);
    }
    @Test(dependsOnMethods ="deleteDocumentTypeById" )
    public void updateDocumentTypeNegative() {
        name = getRandomName();
        SchoolLocations sl=new SchoolLocations();
        sl.setName(name);
        sl.setShortName(shortName);
        sl.setCapacity(capacity);
        sl.school=new School();
        sl.school.id="6343bf893ed01f0dc03a509a";
        sl.setType("CLASS");
        sl.setId(schoolLocationsID);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(sl)
                .when()
                .put("school-service/api/location")
                .then()
                .log().body()
                .statusCode(400)
                .body("message",equalTo("SchoolLocation.ERROR.SCHOOL_LOCATION_NOT_FOUND"));
    }
}
