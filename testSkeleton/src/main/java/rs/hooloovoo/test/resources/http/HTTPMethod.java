package rs.hooloovoo.test.resources.http;

public enum HTTPMethod {
    POST("POST"),
    DELETE("DELETE"),
    PUT("PUT"),
    GET("GET");

    private String method;

    public String getValue() { return method; }

    public String toString(){return this.getValue(); }

    HTTPMethod(String method_name) { this.method = method_name; }
}
