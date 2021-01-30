package com.amazonaws.lambda.playerinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutputObject {
	String userName, gamePlayed, dayPlayed;
	List<Map<String, Object>> playerInformationList = new ArrayList<>();
	
	
	public OutputObject(PlayerInfo currentPlayer) {
		this.userName = currentPlayer.getUserName();
		this.gamePlayed = currentPlayer.getGamePlayed();
		this.dayPlayed = currentPlayer.getDayPlayed();
	}


	public void addPlayerInfo(Map<String, Object> playerInfo) {
		playerInformationList.add(playerInfo);
	}

}
