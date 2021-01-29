package com.amazonaws.lambda.findmatch;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "history_store")
public class PlayerInfo {
	String userName, gamePlayed, dayPlayed;
	int historyId;
	
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

	@Override
	public String toString() {
		return "{userName=" + userName + ", gamePlayed=" + gamePlayed + ", dayPlayed=" + dayPlayed + "}";
	}
	
	
}
