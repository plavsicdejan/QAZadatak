package rs.hooloovoo.test.resources;

public class Init {
    private static String base_url="http://localhost:9762/audit";
    private static String base_url_audit="http://localhost:9762/audit/audit";


    public static String generateAppUrl(ServiceMethod method) {
        StringBuilder url = new StringBuilder(base_url);
        if(method.name().contains("AUDIT")) url = new StringBuilder(base_url_audit);
        return url.append(method.getMethod()).toString();
    }
}
