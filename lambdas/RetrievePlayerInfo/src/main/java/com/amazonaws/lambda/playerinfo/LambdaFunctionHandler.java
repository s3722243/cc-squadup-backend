package com.amazonaws.lambda.playerinfo;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

public class LambdaFunctionHandler implements RequestHandler<Map<String,Object>, Map<String, Object>> {
	static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	static DynamoDB dynamoDB = new DynamoDB(client);
	static DynamoDBMapper mapper = new DynamoDBMapper(client);

	public LambdaFunctionHandler() {}

	public Map<String, Object> handleRequest(Map<String,Object> event, Context context) {
//		Gson g = new Gson();  
//		JsonReader reader = new JsonReader(new StringReader(event.toString()));
//		reader.setLenient(true);
//
//		JsonObject json = g.fromJson(reader, JsonObject.class);

		context.getLogger().log("Received event: " + event);

		Table userInformation = dynamoDB.getTable("user_details");

		List<PlayerInfo> scanResult = mapper.scan(PlayerInfo.class, new DynamoDBScanExpression());
		List<PlayerInfo> items = new ArrayList<>();		

		String username = "Darknight091";

		for(PlayerInfo currentPlayer: scanResult) {
			if (currentPlayer.getUserName().equals(username)) {

				List<String> players = Arrays.asList(currentPlayer.getPlayers().split(","));
				for(String currentPlayedPlayer: players) {

					GetItemSpec playerInformation = new GetItemSpec().withPrimaryKey("username", currentPlayedPlayer);
					currentPlayer.addPlayerInfo(userInformation.getItem(playerInformation).asMap());
				}


				items.add(currentPlayer);
			}
		}

		Map<String, Object> response = new HashMap<>();
		Map<String, String> headers = new HashMap<>();
		response.put("statusCode", 200);
		headers.put("Access-Control-Allow-Origin", "*");
		response.put("isBase64Encoded", false);
		response.put("headers", headers);
		response.put("body", "hi");
		return response;
	}
}