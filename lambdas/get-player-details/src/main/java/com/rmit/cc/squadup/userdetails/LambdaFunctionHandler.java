package com.rmit.cc.squadup.userdetails;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.rmit.cc.squadup.userdetails.model.ApiGatewayProxyRequest;
import com.rmit.cc.squadup.userdetails.model.ApiGatewayProxyResponse;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;

public class LambdaFunctionHandler implements RequestHandler<ApiGatewayProxyRequest, ApiGatewayProxyResponse> {

    private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    private final DynamoDB dynamoDB = new DynamoDB(client);
    private final DynamoDBMapper mapper = new DynamoDBMapper(client);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public ApiGatewayProxyResponse handleRequest(ApiGatewayProxyRequest event, Context context) {

        String username = event.getPathParameters().get("username");
        Table userInformation = dynamoDB.getTable("user_details");

        GetItemSpec playerInformation = new GetItemSpec().withPrimaryKey("username", username);

        Map<String, Object> details = new HashMap<>();

        Item item = userInformation.getItem(playerInformation);
        if (item != null) {
            details.putAll(item.asMap());
        } else {
            details.put("username", username);
        }

        ApiGatewayProxyResponse response = new ApiGatewayProxyResponse();
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        response.setHeaders(headers);
        response.setBody(objectMapper.writeValueAsString(details));

        return response;
    }
}