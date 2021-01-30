package com.rmit.cc.squadup.userdetails.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ApiGatewayProxyResponse {
    private int statusCode = 200;
    private Map<String, String> headers;
    private String body;
    private boolean isBase64Encoded = false;
}

