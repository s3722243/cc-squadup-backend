package com.amazonaws.lambda.saveuser;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;


@Getter
@Setter
@ToString
public class ApiGatewayProxyRequest {
    private String resource;
    private String path;
    private String httpMethod;
    private Map<String, String> headers;
    private Map<String, String> queryStringParameters = new HashMap<>();
    private Map<String, String> pathParameters = new HashMap<>();
    private Map<String, String> stageVariables;
    private RequestContext requestContext;
    private Context context;
    private String body;
    private Boolean isBase64Encoded;
}