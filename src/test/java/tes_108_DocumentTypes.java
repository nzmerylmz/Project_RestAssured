import Model.DocumentTypes;
import Model.PositionCategories;
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

public class tes_108_DocumentTypes {
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
    String documentTypeID;

    @Test
    public void createDocumentType() {
        name = getRandomName();
        DocumentTypes dt=new DocumentTypes();
        dt.setSchoolId("6343bf893ed01f0dc03a509a");
        dt.setName(name);
        dt.setDescription("");
        String[] attachmentStages=new String[1];
        attachmentStages[0]="STUDENT_REGISTRATION";
        dt.setAttachmentStages(attachmentStages);

        documentTypeID =
                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(dt)
                        .when()
                        .post("school-service/api/attachments")
                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().jsonPath().getString("id");
    }
    @Test(dependsOnMethods = "createDocumentType")
    public void updateDocumentType() {
        name = getRandomName();
        DocumentTypes dt=new DocumentTypes();
        dt.setSchoolId("6343bf893ed01f0dc03a509a");
        dt.setName(name);
        dt.setId(documentTypeID);
        dt.setDescription("");
        String[] attachmentStages=new String[1];
        attachmentStages[0  ]="STUDENT_REGISTRATION";
        dt.setAttachmentStages(attachmentStages);

                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(dt)
                        .when()
                        .put("school-service/api/attachments")
                        .then()
                        .log().body()
                        .statusCode(200)
                        .body("name",equalTo(name));
    }
    @Test(dependsOnMethods = "updateDocumentType")
    public void deleteDocumentTypeByID() {
        given()
                .cookies(cookies)
                .pathParam("documentTypeId",documentTypeID)
                .when()
                .delete("school-service/api/attachments/{documentTypeId}")
                .then()
                .log().body()
                .statusCode(200);
    }
    @Test(dependsOnMethods = "deleteDocumentTypeByID")
    public void deleteDocumentTypeByIDNegative() {
        given()
                .cookies(cookies)
                .pathParam("documentTypeId",documentTypeID)
                .when()
                .delete("school-service/api/attachments/{documentTypeId}")
                .then()
                .log().body()
                .statusCode(400);
    }
    @Test(dependsOnMethods = "deleteDocumentTypeByID")
    public void updateDocumentTypeNegative() {
        name = getRandomName();
        DocumentTypes dt=new DocumentTypes();
        dt.setSchoolId("6343bf893ed01f0dc03a509a");
        dt.setName(name);
        dt.setId(documentTypeID);
        dt.setDescription("");
        String[] attachmentStages=new String[1];
        attachmentStages[0]="STUDENT_REGISTRATION";
        dt.setAttachmentStages(attachmentStages);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(dt)
                .when()
                .put("school-service/api/attachments")
                .then()
                .log().body()
                .statusCode(400)
                .body("message",equalTo("GENERAL.ERROR.ATTACHMENT_TYPE_NOT_FOUND"));
    }

}
