package com.amazonaws.lambda.playerinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "history_store")
public class PlayerInfo {
	String userName, gamePlayed, dayPlayed, players;
	int historyId;
	List<Map<String, Object>> playerInformationList = new ArrayList<>();
	
	@DynamoDBHashKey(attributeName = "history_id")
	public int getHistoryId() {
		return historyId;
	}

	public void setHistoryId(int historyId) {
		this.historyId = historyId;
	}

	@DynamoDBAttribute(attributeName = "username")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@DynamoDBAttribute(attributeName = "game_played")
	public String getGamePlayed() {
		return gamePlayed;
	}

	public void setGamePlayed(String gamePlayed) {
		this.gamePlayed = gamePlayed;
	}

	@DynamoDBAttribute(attributeName = "day_played")
	public String getDayPlayed() {
		return dayPlayed;
	}

	public void setDayPlayed(String dayPlayed) {
		this.dayPlayed = dayPlayed;
	}
	

	@DynamoDBAttribute(attributeName = "players")
	public String getPlayers() {
		return players;
	}

	public void setPlayers(String players) {
		this.players = players;
	}
	
	public void addPlayerInfo(Map<String, Object> playerInfo) {
		playerInformationList.add(playerInfo);
	}

	@Override
	public String toString() {
		return "[userName=" + userName + ", gamePlayed=" + gamePlayed + ", dayPlayed=" + dayPlayed
				+ ", playerInformationList=" + playerInformationList + "]";
	}
	
	
	
}
