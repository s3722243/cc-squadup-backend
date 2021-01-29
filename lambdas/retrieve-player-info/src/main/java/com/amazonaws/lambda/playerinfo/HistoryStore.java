package com.amazonaws.lambda.playerinfo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@DynamoDBTable(tableName = "history_store")
public class HistoryStore {

    @DynamoDBHashKey(attributeName = "history_id")
    private int historyId;
    @DynamoDBAttribute(attributeName = "username")
    private String username;
    @DynamoDBAttribute(attributeName = "day_played")
    private String dayPlayed;
    @DynamoDBAttribute(attributeName = "game_id")
    private int gameId;
    @DynamoDBAttribute(attributeName = "players")
    private List<String> players;

    private List<Map<String, Object>> playerInformationList = new ArrayList<>();

    public void addPlayerInfo(Map<String, Object> playerInfo) {
        playerInformationList.add(playerInfo);
    }
}
