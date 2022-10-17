package Model;

import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.matcher.ResponseAwareMatcher;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class DepartmentsTest {
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
    String departmentsId;
    String Name;
    String Code;

    @Test
    public void createDepartments(){

        Name=getRandomName();
        Code=getRandomCode();
        Departments departments= new Departments();
        departments.setName(Name);
        departments.setCode(Code);
        departments.setConstans(new String[]{});
        departments.setSections(new String[]{});
        departments.setSchool(new School("5fe07e4fb064ca29931236a5"));

        departmentsId=
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
        return RandomStringUtils.randomAlphabetic(16).toLowerCase();
    }

    public String getRandomCode() {
        return RandomStringUtils.randomAlphabetic(5).toLowerCase();
    }

    @Test(dependsOnMethods = "createDepartments")
    public void createDepartmentsNegative(){

        Name=getRandomName();
        Code=getRandomCode();
        Departments departments= new Departments();
        departments.setId(departmentsId);
        departments.setName(Name);
        departments.setCode(Code);
        departments.setConstans(new String[]{});
        departments.setSections(new String[]{});
        departments.setSchool(new School("5fe07e4fb064ca29931236a5"));

        given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(departments)
                        .when()
                        .post("school-service/api/department")
                        .then()
                        .log().body()
                        .statusCode(400)
                        .body("message", equalTo("Given school department already created. Please, check department info."))
        ;
    }
    @Test(dependsOnMethods = "createDepartments")
    public void updateDepartments(){

        Name=getRandomName();
        Code=getRandomCode();
        Departments departments= new Departments();
        departments.setId(departmentsId);
        departments.setName(Name);
        departments.setCode(Code);
        departments.setConstans(new String[]{});
        departments.setSections(new String[]{});
        departments.setSchool(new School("5fe07e4fb064ca29931236a5"));

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(departments)
                .when()
                .put("school-service/api/department")
                .then()
                .log().body()
                .statusCode(200)
                .body("code",equalTo(Code))
        ;
    }
    @Test(dependsOnMethods = "updateDepartments")
    public void deleteDepartments(){
        given()
                .cookies(cookies)
                .pathParam("departmentsId",departmentsId)
                .when()
                .delete("school-service/api/department/{departmentsId}")
                .then()
                .log().body()
                .statusCode(204)
        ;
    }
    @Test(dependsOnMethods = "deleteDepartments")
    public void deleteDepartmentsNegative(){
        given()
                .cookies(cookies)
                .pathParam("departmentsId",departmentsId)
                .when()
                .delete("school-service/api/department/{departmentsId}")
                .then()
                .log().body()
                .statusCode(204)
        ;
    }
    @Test(dependsOnMethods = "deleteDepartments")
    public void updateDepartmentsNegative(){

        Name=getRandomName();
        Code=getRandomCode();
        Departments departments= new Departments();
        departments.setId(departmentsId);
        departments.setName(Name);
        departments.setCode(Code);
        departments.setConstans(new String[]{});
        departments.setSections(new String[]{});
        departments.setSchool(new School("5fe07e4fb064ca29931236a5"));

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(departments)
                .when()
                .put("school-service/api/department")
                .then()
                .log().body()
                .statusCode(400)
        ;
    }
}
