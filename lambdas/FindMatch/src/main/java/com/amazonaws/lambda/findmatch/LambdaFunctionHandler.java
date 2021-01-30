package com.amazonaws.lambda.findmatch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
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
	static DynamoDBMapper mapper = new DynamoDBMapper(client);
	
	@Override
	@SneakyThrows
	public ApiGatewayProxyResponse handleRequest(ApiGatewayProxyRequest input, Context context) {
		Gson g = new Gson();  
		
		JsonObject json = g.fromJson(input.getBody(), JsonObject.class);


		System.out.println("This is the body that is being consumed" + input.getBody());
		System.out.println("this is the json object " + json);
		System.out.println("this is the json object " + json.toString());
		String username = json.get("username").getAsString();
		String gameSearching = json.get("game_id").getAsString();
		String regionSearching = json.get("region") != null && !json.get("region").isJsonNull() ? json.get("region").getAsString() : "ANY";
		String consoleSearching = json.get("console") != null && !json.get("console").isJsonNull()? json.get("console").getAsString() : "ANY";
		int playersNeeded = json.get("playersNeeded").getAsInt();


		String body = "Failure";
		CompletableFuture<String> completableFuture  
		=  CompletableFuture.supplyAsync(() -> completableFuture(username, gameSearching, playersNeeded, regionSearching, consoleSearching));

		try {
			body = completableFuture.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		

		ApiGatewayProxyResponse response = new ApiGatewayProxyResponse();
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        response.setHeaders(headers);
        response.setBody(objectMapper.writeValueAsString(body));
        return response;
		
	}

	public String completableFuture(String username, String gameSearching, int playerNeeded, String regionSearching, String consoleSearching) {
		Table searchTable = dynamoDB.getTable("find_match");
		Table playerHistory = dynamoDB.getTable("history_store");
		String allPlayers = null;

		Item currentUserEntry = new Item()
				.withPrimaryKey("username", username)
				.withString("game_id", gameSearching)
				.withString("region", regionSearching)
				.withString("console", consoleSearching)
				.withInt("players_needed", playerNeeded);
		searchTable.putItem(currentUserEntry);

		
		List<GameSearch> playersChosen = new ArrayList<GameSearch>();
		playersChosen.add(new GameSearch(gameSearching,username,null , regionSearching, consoleSearching, playerNeeded));

		long t= System.currentTimeMillis();
		long end = t+120000;
		int playersFound = 1;
		boolean beenFoundAlready = false;
		while((playersFound != playerNeeded && !beenFoundAlready) && t < end) {
			List<GameSearch> scanResult = mapper.scan(GameSearch.class, new DynamoDBScanExpression());
			playersFound = 1;
			for (GameSearch currentUser : scanResult) {
				if (currentUser.getUsername().equals(username)) {
					if (currentUser.getPlayersFound() != null) {
						allPlayers = currentUser.getPlayersFound();
						beenFoundAlready = true;
					}
				}else if(currentUser.getGameId().equals(gameSearching) && 
						(currentUser.getRegion().equals(regionSearching) || regionSearching.equals("ANY") || currentUser.getRegion().equals("ANY")) &&
						(currentUser.getConsole().equals(consoleSearching) || consoleSearching.equals("ANY") || currentUser.getConsole().equals("ANY")) &&
						currentUser.getPlayersNeeded() == playerNeeded && 
						playersFound < playerNeeded && 
						!currentUser.getUsername().equals(username)) {
					playersChosen.add(currentUser);
					playersFound++;
					System.out.println("Added a player");
				}
			}
			t = System.currentTimeMillis();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ignored) {}
		}

		if (playersChosen.size() == playerNeeded || beenFoundAlready) {
			
			DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
					.withPrimaryKey(new PrimaryKey("username", username)).withConditionExpression("game_id <= :val")
					.withValueMap(new ValueMap().withString(":val", gameSearching));

			searchTable.deleteItem(deleteItemSpec);
			if (allPlayers == null) {
				allPlayers= playersChosen.stream().map(mapper -> mapper.getUsername()).collect(Collectors.toList()).toString();
				allPlayers = allPlayers.substring(1, allPlayers.length() -1);
			}
			Map<String, Object> expressionAttributeValues = new HashMap<String, Object>();
			expressionAttributeValues.put(":val1", allPlayers);

			for(GameSearch currentPlayer : playersChosen) {
				if (!currentPlayer.getUsername().equals(username)) {
					searchTable.updateItem(
						    "username",          // key attribute name
						    currentPlayer.getUsername(),           // key attribute value
						    "set players_found = :val1", // UpdateExpression
						    null,
						    expressionAttributeValues);
				}
			}
			
			//writing to the history database, need to find the next valid history_id which is very annoyinggg cause dynamodb wants primary key and we dont have one 
			// :(
			List<PlayerInfo> historyIdFinder = mapper.scan(PlayerInfo.class, new DynamoDBScanExpression());
			
			int maxValue = historyIdFinder.stream().max((PlayerInfo p1, PlayerInfo p2) -> Integer.compare(p1.getHistoryId(), p2.getHistoryId())).get().getHistoryId()+ 1;
			playersChosen.removeIf(filter -> filter.getUsername().equals(username));
			
			String players = playersChosen.stream().map(mapper -> mapper.getUsername()).collect(Collectors.toList()).toString();
			Item item = new Item()
					.withPrimaryKey("history_id", maxValue)
					.withString("username", username)
					.withString("game_played", gameSearching)
					.withString("day_played", LocalDate.now().toString())
					.withString("players", players.substring(1, players.length()-1));
			System.out.println("Writing to the players history");
			playerHistory.putItem(item);

			return players;
		}else
			return "Failure";
	}

}
