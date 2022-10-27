package LiveProject;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@ExtendWith(PactConsumerTestExt.class)

public class ConsumerTest {
    // Create the Headers

    Map<String, String> headers = new HashMap<>();

    // set the resource path
    String resourcepath = "/api/users";

    //create the contract
    @Pact(consumer="UserConsumer", provider="UserProvider")
    public RequestResponsePact createPact(PactDslWithProvider builder){
        //set the headers
        headers.put("Content-Type", "application/json");
        //create the body
        DslPart requestResponseBody = new PactDslJsonBody()
                .numberType("id",456)
                .stringType("firstName","Baljeet")
                .stringType("lastName","Janjua")
                .stringType("email","bajanjua@in.ibm.com");

        //Record interaction to pact
        return builder.given("A request to create a user")
                .uponReceiving("A request to create a user")
                .method("POST")
                .path(resourcepath)
                .headers(headers)
                .body(requestResponseBody)
                .willRespondWith()
                .status(201)
                .body(requestResponseBody)
                .toPact();

    }

    @Test
    @PactTestFor(providerName="UserProvider", port="8282")

    public void consumerTest(){

        //Base URI
        String baseURI = "http://localhost:8282/api/users";
        //Request Body
        Map<String, Object> reqBody = new HashMap<>();
        reqBody.put("id",456);
        reqBody.put("firstName","Baljeet");
        reqBody.put("lastName","Janjua");
        reqBody.put("email","bajanjua@in.ibm.com");

        //Generate response
        given().headers(headers).body(reqBody).log().all().when().post(baseURI).
                then().statusCode(201).log().all();

    }

}
