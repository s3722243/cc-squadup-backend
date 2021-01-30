package com.amazonaws.lambda.playerinfo;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class RequestContext {
    private String resourceId;
    private String resourcePath;
    private String httpMethod;
    private String extendedRequestId;
    private String requestTime;
    private String path;
    private String accountId;
    private String protocol;
    private String stage;
    private String domainPrefix;
    private long requestTimeEpoch;
    private String requestId;
    private Map<String, Object> identity;
    private String domainName;
    private String apiId;
}