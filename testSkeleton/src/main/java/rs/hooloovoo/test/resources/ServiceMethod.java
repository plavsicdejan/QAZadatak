package rs.hooloovoo.test.resources;

public enum ServiceMethod {
    REGISTER_USER("/users/register"),
    GET_USER("/users/%s"),
    GET_AUDIT("/user/%s"),
    ADD_AUDIT("/add"),
    USER_LOGIN("/users/login");

    private String method;

    ServiceMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
