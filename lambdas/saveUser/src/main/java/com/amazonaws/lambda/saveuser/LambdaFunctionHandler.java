package com.amazonaws.lambda.saveuser;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.SneakyThrows;

public class LambdaFunctionHandler implements RequestHandler<ApiGatewayProxyRequest, ApiGatewayProxyResponse> {

	static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	static DynamoDB dynamoDB = new DynamoDB(client);
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @SneakyThrows
    public ApiGatewayProxyResponse handleRequest(ApiGatewayProxyRequest event, Context context) {
    	Gson gson = new Gson();
    	JsonObject jsonObject = gson.fromJson(event.getBody(), JsonObject.class);
    	

    	Table playerInfo = dynamoDB.getTable("user_details");
    	Item currentUserEntry = new Item()
				.withPrimaryKey("username", jsonObject.get("username").getAsString());
    	jsonObject.remove("username");
    	for(String currentAccount : jsonObject.keySet()) {
    		currentUserEntry.withString(currentAccount, jsonObject.get(currentAccount).getAsString());
    	}
    	
    	playerInfo.putItem(currentUserEntry);
        ApiGatewayProxyResponse response = new ApiGatewayProxyResponse();
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        response.setHeaders(headers);
        response.setBody(objectMapper.writeValueAsString("Successfully saved user information"));
        return response;

    }

}
