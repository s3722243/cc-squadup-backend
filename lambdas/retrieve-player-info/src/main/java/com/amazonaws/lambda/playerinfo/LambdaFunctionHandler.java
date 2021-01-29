package com.amazonaws.lambda.playerinfo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LambdaFunctionHandler implements RequestHandler<ApiGatewayProxyRequest, ApiGatewayProxyResponse> {
    private static final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    private static final DynamoDB dynamoDB = new DynamoDB(client);
    private static final DynamoDBMapper mapper = new DynamoDBMapper(client);

    public ApiGatewayProxyResponse handleRequest(ApiGatewayProxyRequest event, Context context) {
        context.getLogger().log("Received event: " + event);
        Table userInformation = dynamoDB.getTable("user_details");

        List<HistoryStore> scanResult = mapper.scan(HistoryStore.class, new DynamoDBScanExpression());
        List<HistoryStore> items = new ArrayList<>();

        String username = event.getPathParameters().get("username");

        for (HistoryStore currentPlayer : scanResult) {
            if (currentPlayer.getUserName().equals(username)) {
//                for (String currentPlayedPlayer : currentPlayer.getPlayers()) {
//                    GetItemSpec playerInformation = new GetItemSpec().withPrimaryKey("username", currentPlayedPlayer);
//                    currentPlayer.addPlayerInfo(userInformation.getItem(playerInformation).asMap());
//                }
                items.add(currentPlayer);
            }
        }

        ApiGatewayProxyResponse response = new ApiGatewayProxyResponse();
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        response.setHeaders(headers);
        response.setBody(items);

        return response;
    }
}