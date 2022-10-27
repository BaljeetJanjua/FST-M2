package LiveProject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.oauth2;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;


public class GitHubProject {
    RequestSpecification requestSpec;
    ResponseSpecification responseSpec;
    int idSSH;

    String keySSH = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCsQyTYuPmYK/tbf83v/J02cozTJgN8x5RolxQ7dYj2j3L01WQQxTBBV6ivsjyMLKl6FldXt4aS3gkm4euQeXxMQTRANMcgZXqohCTWAKSNymJGeTgSRlkpkQDXycjXDVPDc3fffj4TjUyx20HBw5vGWW14fKXOt+3P1FmQhZagXODuGRf/IziBHTvWC82+GHr7Es7hsE5gWv7FuGMYbPEy9BkXAr31NwgPVL/wR/5Owefk7fLLw0f+YogHggXz/2mZPaIbzV1CA4+s3oQkg02IS297pysl5DRBY0eykXZ+IRd7nxMrsez7gWAPPau+9SbpyS7KKioH+Di07kddUdyp";

    @BeforeClass
    public void setUp() {
        // Create request specification
        requestSpec = new RequestSpecBuilder()
                //Enter the request details where authorisation will have the generated token value from GitHUb
                .setContentType(ContentType.JSON)
                .setAuth(oauth2("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"))
                .setBaseUri("https://api.github.com")
                .build();

        // Create response specification
        responseSpec = new ResponseSpecBuilder()
                // Check response content type
                .expectContentType("application/json")
                // Check if response time is less than 4s
                .expectResponseTime(lessThan(4000L))
                // Build response specification
                .build();
    }
    @Test (priority =1)

    public void PostRequestTest(){
        //Request Body
        Map<String,Object> reqBody = new HashMap<>();
        reqBody.put("title","TestAPIKey");
        reqBody.put("key",keySSH);

        //Generate Response

        Response response = given().spec(requestSpec).body(reqBody).when().request("post", "/user/keys"); // Use requestSpec
        System.out.println(response.getBody().asPrettyString());

        //extract the ID from posted request

        idSSH = response.then().extract().path("id");

        System.out.println("extracted Id is " + idSSH);

        response.then().spec(responseSpec).statusCode(201).body("title",equalTo("TestAPIKey"));

    }

    @Test(priority = 2)

    public void GetRequestTest(){

        //Generate response and assert
        given().spec(requestSpec).pathParam("id",idSSH).when().get("/user/keys/{id}").
                // usage of log all for reporting
        then().spec(responseSpec).log().all().statusCode(200);
    }

    @Test (priority =3)

    public void DeleteRequestTest(){

        //Generate response and assert
        given().spec(requestSpec).pathParam("id",idSSH).when().delete("/user/keys/{id}").
                // usage of log all for reporting and assertion for status code as 204
                        then().log().all().statusCode(204);
        // usage of reporter.log
        String ResponseBody = responseSpec.response().toString();
        Reporter.log("body of delete request" +ResponseBody );
    }
}