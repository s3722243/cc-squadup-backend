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
import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class LambdaFunctionHandler implements RequestHandler<Object, String> {
	static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	static DynamoDB dynamoDB = new DynamoDB(client);

	static DynamoDBMapper mapper = new DynamoDBMapper(client);
	@Override
	public String handleRequest(Object input, Context context) {
		System.out.println(input.getClass());
		Gson g = new Gson();  
		System.out.println("This is the input : " + input.toString());
		JsonObject json = g.fromJson(input.toString(), JsonObject.class);

		String username = json.get("Username").getAsString();
		String gameSearching = json.get("game").getAsString();
		int playersNeeded = json.get("playersNeeded").getAsInt();



		CompletableFuture<String> completableFuture  
		=  CompletableFuture.supplyAsync(() -> completableFuture(username, gameSearching, playersNeeded));

		try {
			return completableFuture.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Failure";
	}

	public String completableFuture(String username, String gameSearching, int playerNeeded) {
		Table searchTable = dynamoDB.getTable("searchTables");
		Table playerHistory = dynamoDB.getTable("history_store");
		String allPlayers = null;

		Item currentUserEntry = new Item()
				.withPrimaryKey("username", username)
				.withString("Game", gameSearching)
				.withInt("players_needed", playerNeeded);
		searchTable.putItem(currentUserEntry);

		
		List<GameSearch> playersChosen = new ArrayList<GameSearch>();
		playersChosen.add(new GameSearch(gameSearching,username, playerNeeded));

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
				}else if(currentUser.getGameName().equals(gameSearching) && 
						currentUser.getPlayerNeeded() == playerNeeded && 
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
					.withPrimaryKey(new PrimaryKey("username", username)).withConditionExpression("Game <= :val")
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
