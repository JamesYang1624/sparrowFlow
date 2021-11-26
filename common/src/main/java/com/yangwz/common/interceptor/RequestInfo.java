package com.yangwz.common.interceptor;

import java.util.List;
import java.util.Map;

/**
 * @author : yangweizheng
 * @date : 2021/2/25 17:15
 */
public class RequestInfo {
    private String url;
    private Map<String, List<String>> headers;
    private String body;
    private String fullRequest;
    private String peekBody;

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFullRequest() {
        return fullRequest;
    }

    public void setFullRequest(String fullRequest) {
        this.fullRequest = fullRequest;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPeekBody() {
        return peekBody;
    }

    public void setPeekBody(String peekBody) {
        this.peekBody = peekBody;
    }

    @Override
    public String toString() {
        return "RequestInfo{" +
                "url='" + url + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                ", fullRequest='" + fullRequest + '\'' +
                ", peekBody='" + peekBody + '\'' +
                '}';
    }
}