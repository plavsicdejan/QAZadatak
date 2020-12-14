package rs.hooloovoo.test;

import rs.hooloovoo.test.model.CreatedAudit;
import rs.hooloovoo.test.model.CreatedUser;
import rs.hooloovoo.test.model.GetAudit;
import rs.hooloovoo.test.resources.Init;
import rs.hooloovoo.test.resources.ServiceMethod;
import rs.hooloovoo.test.resources.Users;
import rs.hooloovoo.test.resources.http.HTTPMethod;
import rs.hooloovoo.test.resources.http.HTTPRequest;
import rs.hooloovoo.test.resources.http.HTTPResponse;
import rs.hooloovoo.test.resources.map.Mapper;

import java.util.List;
import java.util.Random;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

public class Methods {
    public static CreatedUser createUser(String username, String password) throws Exception {
        String body="{\"username\": \""+username+"\",\n" +
                " \"password\": \""+password+"\"}";
        String url= Init.generateAppUrl(ServiceMethod.REGISTER_USER);
//        System.out.println(body);
        HTTPRequest request = new HTTPRequest(url).setMethod(HTTPMethod.PUT)
                .setContentType("application/json").setBody(body);
        HTTPResponse response = request.sendRequest();
        if (!response.code.equals(201))
            throw new Exception("Creating customer failed. Code: " + response.getCode() + " | Message: " + response.getBody());
        return Mapper.mapFromJSON(response.body, CreatedUser.class);
    }
    public static CreatedAudit createAudit(String username, String action) throws Exception {
        String url= Init.generateAppUrl(ServiceMethod.ADD_AUDIT);
        HTTPRequest request = new HTTPRequest(url+"?action="+action+"&username="+username).setMethod(HTTPMethod.PUT)
                .setContentType("application/json");
        HTTPResponse response = request.sendRequest();
        if (!response.code.equals(201))
            throw new Exception("Add audit action"+action+" failed. Code: " + response.getCode() + " | Message: " + response.getBody());
        return Mapper.mapFromJSON(response.body, CreatedAudit.class);
    }
    public static List<GetAudit> getAudit(String username) throws Exception {
        String url= Init.generateAppUrl(ServiceMethod.GET_AUDIT);
        HTTPRequest request = new HTTPRequest(format(url,username)+"?username="+username)
                .setMethod(HTTPMethod.GET).setContentType("application/json");
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", 200, response.code.intValue());
        return Mapper.mapFromJsonArray(response.body, GetAudit[].class);
        }
    public static int getUserCode(String username) throws Exception {
        String url= Init.generateAppUrl(ServiceMethod.GET_USER);
        HTTPRequest request = new HTTPRequest(format(url,username))
                .setMethod(HTTPMethod.GET).setContentType("application/json");
        HTTPResponse response = request.sendRequest();
        return response.code.intValue();
    }
    public static CreatedUser getUser(String username) throws Exception {
        String url= Init.generateAppUrl(ServiceMethod.GET_USER);
        HTTPRequest request = new HTTPRequest(format(url,username))
                .setMethod(HTTPMethod.GET).setContentType("application/json");
        HTTPResponse response = request.sendRequest();
        if (!response.code.equals(200))
            throw new Exception("Get user action failed. Code: " + response.getCode() + " | Message: " + response.getBody());
        return Mapper.mapFromJSON(response.body, CreatedUser.class);

    }
    public static String generateString(int length) {
        String sourceString = "qwertyuiopasdfghjklzxcvbnm0123456789QWERTYUIOPASDFGHJKLZXCVBNM0123456789QWERTYUIOPASDFGHJKLZXCVBNM0123456789qwertyuiopasdfghjklzxcvbnm";
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= length; i++) {
            sb.append(sourceString.charAt(new Random().nextInt(sourceString.length() - 1)));
        }
        return sb.toString();
    }
    public static Users checkFixedUser() throws Exception {
        Users user=new Users();
        if (!(Methods.getUserCode(user.getUsername()) == 200)) {
            CreatedUser createdUser =Methods.createUser(user.getUsername(),user.getPassword());
            user.user_id=createdUser.getId();
            user.created_at=createdUser.getCreatedAt();
        }else {
            CreatedUser userExist = Methods.getUser(user.getUsername());
            user.user_id=userExist.getId();
            user.created_at=userExist.getCreatedAt();
        }
        return user;
    }
}
