package rs.hooloovoo.test;

import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import rs.hooloovoo.test.model.CreatedUser;
import rs.hooloovoo.test.resources.Init;
import rs.hooloovoo.test.resources.JDBC_Utils;
import rs.hooloovoo.test.resources.ServiceMethod;
import rs.hooloovoo.test.resources.Users;
import rs.hooloovoo.test.resources.http.HTTPMethod;
import rs.hooloovoo.test.resources.http.HTTPRequest;
import rs.hooloovoo.test.resources.http.HTTPResponse;
import rs.hooloovoo.test.resources.map.Mapper;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

public class GetUserTest {
    private static String url= Init.generateAppUrl(ServiceMethod.GET_USER),username,password;
    private static CreatedUser createdUser;
    public static List<String> ids= new ArrayList<>();
    public static Users user;

    @BeforeClass
    public static void before() throws Exception {
        user=Methods.checkFixedUser();
        username=user.getUsername();
        password= user.getPassword();
        System.out.println(user.getUser_id().toString());
    }

    @Test
    public static void aRegularCall() throws Exception {
        HTTPRequest request = new HTTPRequest(format(url,username)).setMethod(HTTPMethod.GET).setContentType("application/json");
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", 200, response.code.intValue());
        CreatedUser getUser = Mapper.mapFromJSON(response.body, CreatedUser.class);
        assertEquals(user.getUser_id(),getUser.getId());
        assertEquals(user.getUsername(),getUser.getUsername());
//        assertEquals("Created times are different",new Users().getCreated_at(),getUser.getCreatedAt()); //BUG Vremena kada je kreiran i vreme kada je kreiran ali iz GET poziva, nisu ista
    }
    @Test
    public static void nonExitsingUser() throws Exception {
        String username2=username+2;
        HTTPRequest request = new HTTPRequest(format(url,username2)).setMethod(HTTPMethod.GET).setContentType("application/json");
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", 404, response.code.intValue());
        assertEquals("Body exist", 0, response.body.length());
    }
    @Test
    public static void usernameToLowcase() throws Exception {
        String username2=username.toLowerCase();
        HTTPRequest request = new HTTPRequest(format(url,username2)).setMethod(HTTPMethod.GET).setContentType("application/json");
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", 404, response.code.intValue());
        assertEquals("Body exist", 0, response.body.length());
    }
    @Test
    public static void usernameToUPcase() throws Exception {
        String username2=username.toUpperCase();
        HTTPRequest request = new HTTPRequest(format(url,username2)).setMethod(HTTPMethod.GET).setContentType("application/json");
        HTTPResponse response = request.sendRequest();
        System.out.println(response.body);
        assertEquals("Bad response code: ", 404, response.code.intValue());
        assertEquals("Body exist", 0, response.body.length());
    }

    @DataProvider
    public Object[][] userNameSpecific() {
        return new Object[][]{
//                {"???????"}, //BUG Vraca body {"timestamp":"2020-12-11T10:55:44.312+0000","status":404,"error":"Not Found","message":"No message available","path":"/audit/users/"} dok regularni nepostojeci useri ne vracaju body
                {"!@#$$$**--"},
        };
    }

    @Test(dataProvider = "userNameSpecific")
    public static void usernameSpec(String specUser) throws Exception {
        createdUser=Methods.createUser(specUser,password);
        ids.add(createdUser.getId().toString());
        HTTPRequest request = new HTTPRequest(format(url,specUser)).setMethod(HTTPMethod.GET).setContentType("application/json");
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", 404, response.code.intValue());
        assertEquals("Body exist", 0, response.body.length());
        JDBC_Utils.deleteUserFromDB(JDBC_Utils.collectUsers(ids));
    }
    @DataProvider
    public Object[][] methods() {
        return new Object[][] {
                {"POST"}, { "PUT"}, {"DELETE"}
        };
    }
    @Test(dataProvider = "methods")
    public static void allMethods(String method) throws Exception {
        HTTPRequest request = new HTTPRequest(format(url,username)).setMethod(HTTPMethod.valueOf(method)).setContentType("application/json");
        System.out.println(request.getMethod());
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", 405, response.code.intValue());
        Assertions.assertThat(response.getCodeMessage().contains("Request method "+ method+" not supported"));
    }
}
