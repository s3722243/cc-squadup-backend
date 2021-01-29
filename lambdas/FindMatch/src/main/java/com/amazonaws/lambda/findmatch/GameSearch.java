package com.amazonaws.lambda.findmatch;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "find_match")
public class GameSearch {

	private String gameId, username, playersFound, region, console;
	private int playersNeeded;
	
	
	
	public GameSearch(String gameId, String username, String playersFound, String region, String console,
			int playersNeeded) {
		super();
		this.gameId = gameId;
		this.username = username;
		this.playersFound = playersFound;
		this.region = region;
		this.console = console;
		this.playersNeeded = playersNeeded;
	}
	
	public GameSearch() {
	}


	@DynamoDBHashKey(attributeName = "game_id")
	public String getGameId() {
		return gameId;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	
	@DynamoDBAttribute(attributeName = "username")
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	@DynamoDBAttribute(attributeName = "players_needed")
	public int getPlayersNeeded() {
		return playersNeeded;
	}
	public void setPlayersNeeded(int playersNeeded) {
		this.playersNeeded = playersNeeded;
	}
	
	@DynamoDBAttribute(attributeName = "players_found")
	public String getPlayersFound() {
		return playersFound;
	}
	public void setPlayersFound(String playersFound) {
		this.playersFound = playersFound;
	}
	
	

	@DynamoDBAttribute(attributeName = "region")
	public String getRegion() {
		return region;
	}


	public void setRegion(String region) {
		this.region = region;
	}

	@DynamoDBAttribute(attributeName = "console")
	public String getConsole() {
		return console;
	}


	public void setConsole(String console) {
		this.console = console;
	}


	@Override
	public String toString() {
		return "GameSearch [gameName=" + gameId + ", username=" + username + ", playerNeeded=" + playersNeeded + "]";
	}
	
	
}
