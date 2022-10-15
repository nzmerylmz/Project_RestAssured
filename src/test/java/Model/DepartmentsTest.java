package Model;

import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class DepartmentsTest {

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

    String departmentsID;
    String countryName;
    String countryCode;

    boolean Active;
    public  Map<String ,String> schoolId(){
        Map<String,String > school = new HashMap<>();
        school.put("id","5fe07efb064ca29931236a5");

        return school;
    }


    @Test
    public void createDepartments() {
        countryName = getRandomName();
        countryCode = getRandomCode();
        Departments departments = new Departments();
        departments.setName(countryName); // generateCountrName
        departments.setCode(countryCode); // generateCountrCode
        departments.setSchoolId(schoolId());
        departments.setActive(true);


        departmentsID =
                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(departments)

                        .when()
                        .post("school-service/api/department")

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
        return RandomStringUtils.randomAlphabetic(5).toLowerCase();
    }


}