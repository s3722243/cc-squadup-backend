package com.amazonaws.lambda.playerinfo;

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
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

public class LambdaFunctionHandler implements RequestHandler<ApiGatewayProxyRequest, ApiGatewayProxyResponse > {
	static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	static DynamoDB dynamoDB = new DynamoDB(client);
	static DynamoDBMapper mapper = new DynamoDBMapper(client);
    private final ObjectMapper objectMapper = new ObjectMapper();

	public LambdaFunctionHandler() {}

	@SneakyThrows
	public ApiGatewayProxyResponse  handleRequest(ApiGatewayProxyRequest event, Context context) {

		context.getLogger().log("Received event: " + event);

		Table userInformation = dynamoDB.getTable("user_details");

		List<PlayerInfo> scanResult = mapper.scan(PlayerInfo.class, new DynamoDBScanExpression());
		List<PlayerInfo> items = new ArrayList<>();		

		String username = event.getPathParameters().get("username");

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

		ApiGatewayProxyResponse response = new ApiGatewayProxyResponse();
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        response.setHeaders(headers);
        response.setBody(objectMapper.writeValueAsString(items));
        
		return response;
	}
}