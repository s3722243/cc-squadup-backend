package com.amazonaws.lambda.findmatch;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "searchTables")
public class GameSearch {

	private String gameName, username, playersFound;
	private int playerNeeded;
	
	
	
	public GameSearch(String gameName, String username, int playerNeeded) {
		super();
		this.gameName = gameName;
		this.username = username;
		this.playerNeeded = playerNeeded;
	}
	
	
	public GameSearch() {
		super();
	}


	@DynamoDBHashKey(attributeName = "Game")
	public String getGameName() {
		return gameName;
	}
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}
	
	@DynamoDBAttribute(attributeName = "username")
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	@DynamoDBAttribute(attributeName = "players_needed")
	public int getPlayerNeeded() {
		return playerNeeded;
	}
	public void setPlayerNeeded(int playerNeeded) {
		this.playerNeeded = playerNeeded;
	}
	
	@DynamoDBAttribute(attributeName = "players_found")
	public String getPlayersFound() {
		return playersFound;
	}
	public void setPlayersFound(String playersFound) {
		this.playersFound = playersFound;
	}


	@Override
	public String toString() {
		return "GameSearch [gameName=" + gameName + ", username=" + username + ", playerNeeded=" + playerNeeded + "]";
	}
	
	
}
