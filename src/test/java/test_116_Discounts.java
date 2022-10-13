import Model.Discounts;
import Model.Fields;
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

public class test_116_Discounts {

    public String getRandomDescription() {
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

    private String dicountsId;

    private String dicountsDescription;

    private String dicountsCode;

    private String dicountsPeriority;

    @Test
    public void createDiscount() {

        dicountsDescription = getRandomDescription();
        dicountsCode = getRandomCode();

        Discounts discounts = new Discounts();
        discounts.setDescription(dicountsDescription);
        discounts.setCode(dicountsCode);

        dicountsId =
                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(discounts)

                        .when()
                        .post("school-service/api/discounts")
                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().jsonPath().getString("id");

    }

    @Test(dependsOnMethods = "createDiscount")
    public void createDiscountsNegative() {

        Discounts discounts = new Discounts();
        discounts.setDescription(dicountsDescription);
        discounts.setCode(dicountsCode);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(discounts)

                .when()
                .post("school-service/api/discounts")
                .then()
                .log().body()
                .statusCode(400);

    }
    @Test(dependsOnMethods ="createDiscount" )
    public void updateDiscount(){
        dicountsDescription=getRandomDescription();


        Discounts discounts = new Discounts();
        discounts.setId(dicountsId);
        discounts.setDescription(dicountsDescription);
        discounts.setCode(dicountsCode);



        given()

                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(discounts)

                .when()
                .put("school-service/api/discounts")

                .then()
                .log().body()
                .statusCode(200)
                .body("description",equalTo(dicountsDescription));


    }
    @Test(dependsOnMethods = "updateDiscount")
    public void deleteDiscountById()
    {
        given()
                .cookies(cookies)
                .pathParam("dicountsId", dicountsId)

                .when()
                .delete("school-service/api/discounts/{dicountsId}")

                .then()
                .log().body()
                .statusCode(200)
        ;
    }
    @Test(dependsOnMethods = "deleteDiscountById")
    public void deleteDiscountByIdNegative()
    {
        given()
                .cookies(cookies)
                .pathParam("dicountsId", dicountsId)

                .when()
                .delete("school-service/api/discounts/{dicountsId}")

                .then()
                .log().body()
                .statusCode(400)
        ;
    }
    @Test(dependsOnMethods = "deleteDiscountById")
    public void updateDiscountNegative() {
        dicountsDescription=getRandomDescription();


        Discounts discounts = new Discounts();
        discounts.setId(dicountsId);
        discounts.setDescription(dicountsDescription);
        discounts.setCode(dicountsCode);



        given()

                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(discounts)

                .when()
                .put("school-service/api/discounts")

                .then()
                .log().body()
                .statusCode(400)


                .body("message", equalTo("Discount.Error.DISCOUNT_NOT_FOUND"))
        ;
    }
}
