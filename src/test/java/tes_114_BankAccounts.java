import Model.BankAccounts;
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

public class tes_114_BankAccounts {
    public String getRandomName() {
        return RandomStringUtils.randomAlphabetic(8);
    }

    public String getIntegrationCode() {
        return RandomStringUtils.randomNumeric(3);
    }

    public String getIbanNumber() {
        return RandomStringUtils.randomNumeric(20);
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
    String bankAccountID;
    String integrationCode;
    String iban;

    @Test
    public void createBankAccount() {
        name = getRandomName();
        integrationCode = getIntegrationCode();
        BankAccounts ba = new BankAccounts();
        ba.setName(name);
        ba.setIntegrationCode(integrationCode);
        ba.setCurrency("USD");
        iban = "TR" + getIbanNumber();
        ba.setIban(iban);
        ba.setSchoolID("5fe07e4fb064ca29931236a5");

        bankAccountID =
                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(ba)
                        .when()
                        .post("school-service/api/bank-accounts")
                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().jsonPath().getString("id");
    }

    @Test(dependsOnMethods = "createBankAccount")
    public void createBankAccountNegative() {
        BankAccounts ba = new BankAccounts();
        ba.setName(name);
        ba.setIntegrationCode(integrationCode);
        ba.setCurrency("USD");
        ba.setIban(iban);
        ba.setSchoolID("5fe07e4fb064ca29931236a5");

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(ba)
                .when()
                .post("school-service/api/bank-accounts")
                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("The Bank Account with IBAN \"" + iban + "\" already exists."));
    }

    @Test(dependsOnMethods = "createBankAccount")
    public void updateBankAccount() {
        name=getRandomName();
        BankAccounts ba = new BankAccounts();
        ba.setName(name);
        ba.setIntegrationCode(integrationCode);
        ba.setCurrency("USD");
        ba.setIban(iban);
        ba.setId(bankAccountID);
        ba.setSchoolID("5fe07e4fb064ca29931236a5");

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(ba)
                .when()
                .put("school-service/api/bank-accounts")
                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(name));
    }
    @Test(dependsOnMethods = "updateBankAccount")
    public void deleteBankAccountByID() {

        given()
                .cookies(cookies)
                .pathParam("bankAccountID",bankAccountID)
                .when()
                .delete("school-service/api/bank-accounts/{bankAccountID}")
                .then()
                .log().body()
                .statusCode(200);
    }
    @Test(dependsOnMethods = "deleteBankAccountByID")
    public void deleteBankAccountNegative() {

        given()
                .cookies(cookies)
                .pathParam("bankAccountID",bankAccountID)
                .when()
                .delete("school-service/api/bank-accounts/{bankAccountID}")
                .then()
                .log().body()
                .statusCode(400);
    }
    @Test(dependsOnMethods = "deleteBankAccountByID")
    public void updateBankAccountNegative() {
        name=getRandomName();
        BankAccounts ba = new BankAccounts();
        ba.setName(name);
        ba.setIntegrationCode(integrationCode);
        ba.setCurrency("USD");
        ba.setIban(iban);
        ba.setId(bankAccountID);
        ba.setSchoolID("5fe07e4fb064ca29931236a5");

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(ba)
                .when()
                .put("school-service/api/bank-accounts")
                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("Can't find Bank Account"));
    }


}
