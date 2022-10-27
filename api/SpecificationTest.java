package examples;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;

public class SpecificationTest {

    // Declare request and response specifications
    RequestSpecification requestSpec;
    ResponseSpecification responseSpec;
    int petId;
    @BeforeClass
    public void setUpReq() {
        // Create request specification
        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://petstore.swagger.io/v2/pet")
                .build();
    }

    @BeforeClass
    public void setUpRes() {
        // Create response specification
        responseSpec = new ResponseSpecBuilder()
                // Check status code in response
                .expectStatusCode(200)
                // Check response content type
                .expectContentType("application/json")
                // Check if response contains name property
                .expectResponseTime(lessThan(4000L))
                // Build response specification
                .build();
    }

    @Test(priority=1)
    public void PostRequestTest(){
        //Request Body
        Map<String,Object> reqBody = new HashMap<>();
        reqBody.put("id",72593);
        reqBody.put("name","Baljeet");
        reqBody.put("status","alive");

        //Generate Response
        Response response = given().spec(requestSpec).body(reqBody).when().post(); // Use requestSpec
        System.out.println(response.getBody().asPrettyString());
        //extract the pet iD

        petId = response.then().extract().path("id");

        response.then().spec(responseSpec).body("status",equalTo("alive"));
    }

    @Test(priority = 4)
    public void GetRequestTest(){
        //Generate response and assert
         given().spec(requestSpec).pathParam("petId",petId).
                 when().get("/{petId}").
                 // usage of log all for reporting
                 then().spec(responseSpec).log().all().body("status",equalTo("alive"));
    }


    @Test(priority =5)
    public void DeleteRequestTest(){
        //Generate response and assert
        given().spec(requestSpec).pathParam("petId",petId).
                when().delete("/{petId}").
                then().spec(responseSpec).body("message",equalTo("" +petId));
    }

    @Test(priority =2)
    public void testPet1() {
        Response response = given().spec(requestSpec) // Use requestSpec
                       .pathParam("petId", "72593") // Set path parameter
                       .get("/{petId}"); // Send GET request

        // Print response
        String body = response.getBody().asPrettyString();
        System.out.println(body);

        // Assertion
        response.then().body("name", equalTo("Baljeet"));
    }

    @Test(priority =3)
    public void testPet2() {
        Response response =
                given().spec(requestSpec) // Use requestSpec
                        .pathParam("petId", "72593") // Set path parameter
                        .get("/{petId}"); // Send GET request

        // Print response
        String body = response.getBody().asPrettyString();
        System.out.println(body);

        // Assertion
        response.then().body("id", equalTo(72593));
    }
}