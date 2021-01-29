package com.amazonaws.lambda.saveuser;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class LambdaFunctionHandler implements RequestHandler<Object, String> {

	static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	static DynamoDB dynamoDB = new DynamoDB(client);
	
    @Override
    public String handleRequest(Object input, Context context) {
    	Gson gson = new Gson();
    	Table playerInfo = dynamoDB.getTable("user_details");
    	JsonObject userInput = gson.fromJson(input.toString(), JsonObject.class);
    	String username = userInput.get("username").getAsString();
    	Item currentUserEntry = new Item()
				.withPrimaryKey("username", username);
    	for(String currentAccount : userInput.keySet()) {
    		currentUserEntry.withString(currentAccount, userInput.get(currentAccount).getAsString());
    	}
    	
    	playerInfo.putItem(currentUserEntry);
    	
    	
        context.getLogger().log("Input: " + input);

        return "Successfully implemented players information";
    }

}
