package rs.hooloovoo.test;

import org.assertj.core.api.Assertions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
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
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class CreateUserTest {
    private static String url = Init.generateAppUrl(ServiceMethod.REGISTER_USER), body, username, password = new Users().getPassword();
    public static List<String> ids = new ArrayList<>();

    @BeforeMethod
    public static void init() {
        username = "USER" + new Random().nextInt(900000) + "@mail.com";
        body = "{\"username\": \"" + username + "\",\n" +
                " \"password\": \"" + password + "\"}";
    }

    @AfterClass
    public static void after() {
        JDBC_Utils.deleteUserFromDB(JDBC_Utils.collectUsers(ids));
    }

    @Test
    public void aRegularCall() throws Exception {
        HTTPRequest request = new HTTPRequest(url).setMethod(HTTPMethod.PUT)
                .setContentType("application/json").setBody(body);
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", 201, response.code.intValue());
        CreatedUser user = Mapper.mapFromJSON(response.body, CreatedUser.class);
        ids.add(user.getId().toString());
        assertEquals("Bad usernames.", username, user.getUsername());
        Assert.assertNotNull(user.getCreatedAt(), "CreatedAt is null");
        Assert.assertNotNull(user.getId(), "ID is null");
        Assert.assertTrue(user.getId() > 0, "Id is negative");
    }

    @DataProvider
    public Object[][] userNameSpecific() {
        return new Object[][]{
                {Methods.generateString(6), 201},
                {"!@#$%^&", 201},
                {"123456", 201},
                {"VELIKA_SLOVA", 201},
                {"mala_slova", 201},
                {"mix_SLoVA", 201},
                {"mix_#@!#%$_555-333", 201},
                {"*?*?*?*?", 201},
        };
    }

    @Test(dataProvider = "userNameSpecific")
    public void aRegularCallSpecific(String usernameS, Integer code) throws Exception {
        String bodySpecUsername = body.replace(username, usernameS);
        HTTPRequest request = new HTTPRequest(url).setMethod(HTTPMethod.PUT)
                .setContentType("application/json").setBody(bodySpecUsername);
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", code.intValue(), response.code.intValue());
        CreatedUser user = Mapper.mapFromJSON(response.body, CreatedUser.class);
        ids.add(user.getId().toString());
        assertEquals("Bad usernames.", usernameS, user.getUsername());
        Assert.assertNotNull(user.getCreatedAt(), "CreatedAt is null");
        Assert.assertNotNull(user.getId(), "ID is null");
        Assert.assertTrue(user.getId() > 0, "Id is negative");
    }

    @DataProvider
    public Object[][] badUserN() {
        return new Object[][]{
//                { Init.generateString(5),400,"Username must not be longer that 50 characters or shorter than 6 characters."},//BUG username od 5 karaktera prolazi
                {Methods.generateString(4), 400, "Username must not be longer that 50 characters or shorter than 6 characters."},
                {Methods.generateString(51), 400, "Username must not be longer that 50 characters or shorter than 6 characters."},
//                {Init.generateString(50), 201, "could not execute statement; SQL [n/a]; nested exception is org.hibernate.exception.DataException: could not execute statement"},//BUG 50 treba da prodje
//                {Init.generateString(21), 201, "could not execute statement; SQL [n/a]; nested exception is org.hibernate.exception.DataException: could not execute statement"},//BUG preko 20 karaktera ne prolazi
                {"", 400, "Username must not be longer that 50 characters or shorter than 6 characters."},
                {"!@#$", 400, "Username must not be longer that 50 characters or shorter than 6 characters."},

        };
    }

    @Test(dataProvider = "badUserN")
    public void usernameCheck(String bUsername, Integer code, String message) throws Exception {
        String bodyBadUsername = body.replace(username, bUsername);
        System.out.println(bodyBadUsername);
        HTTPRequest request = new HTTPRequest(url).setMethod(HTTPMethod.PUT)
                .setContentType("application/json").setBody(bodyBadUsername);
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", code.intValue(), response.code.intValue());
        assertEquals("Bad response body: ", message, response.body);
    }

    @Test(enabled = false)
    public void usernameMissing() throws Exception {
        String bodyMissingsername = "{\"password\": \"" + password + "\" }";
        HTTPRequest request = new HTTPRequest(url).setMethod(HTTPMethod.PUT)
                .setContentType("application/json").setBody(bodyMissingsername);
        HTTPResponse response = request.sendRequest();
        //BUG {"timestamp":"2020-12-10T14:44:29.782+0000","status":500,"error":"Internal Server Error","message":"No message available","path":"/audit/users/register"}
        assertEquals("Bad response code: ", 400, response.code.intValue());
        assertEquals("Bad response body: ", "Username is missing", response.body);
    }

    @DataProvider
    public Object[][] badPass() {
        return new Object[][]{
                {"!@#$%^&", 400},
                {"123456", 400},
                {"VELIKA_SLOVA", 400},
                {"mala_slova", 400},
                {"mix_SLoVA", 400},
                {"mix_#@!#%$_555-333", 400},
                {"*?*?*?*?", 400},
                {"", 400},
                {"!@#$", 400},
        };
    }

    @Test(dataProvider = "badPass", enabled = false) //BUG Validacija za sifru ne radi uopste
    public void passwordCheck(String bPass, Integer code) throws Exception {
        String bodyBadPass = body.replace(password, bPass);
        HTTPRequest request = new HTTPRequest(url).setMethod(HTTPMethod.PUT)
                .setContentType("application/json").setBody(bodyBadPass);
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", code.intValue(), response.code.intValue());
    }

    @Test(enabled = false)
    public void passMissing() throws Exception {
        String bodyMissingPass = "{\"username\": \"" + username + "\"}";
        HTTPRequest request = new HTTPRequest(url).setMethod(HTTPMethod.PUT)
                .setContentType("application/json").setBody(bodyMissingPass);
        HTTPResponse response = request.sendRequest();
        //BUG {"timestamp":"2020-12-10T16:18:21.159+0000","status":500,"error":"Internal Server Error","message":"No message available","path":"/audit/users/register"}
        assertEquals("Bad response code: ", 400, response.code.intValue());
        assertEquals("Bad response body: ", "Password is missing", response.body);
    }

    @Test
    public void alreadyUsedUsername() throws Exception {
        HTTPRequest request = new HTTPRequest(url).setMethod(HTTPMethod.PUT)
                .setContentType("application/json").setBody(body);
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", 201, response.code.intValue());
        CreatedUser user = Mapper.mapFromJSON(response.body, CreatedUser.class);
        ids.add(user.getId().toString());
        HTTPResponse response2 = request.sendRequest();
        assertEquals("Bad response code: ", 400, response2.code.intValue());
        assertEquals("Bad response body: ", "User with the specified username already exists!", response2.body);
    }
    @DataProvider
    public Object[][] methods() {
        return new Object[][] {
                {"GET"}, { "POST"}, {"DELETE"}
        };
    }
    @Test(dataProvider = "methods",enabled = false)
    public static void allMethods(String method) throws Exception {
        HTTPRequest request = new HTTPRequest(url).setMethod(HTTPMethod.valueOf(method))
                .setContentType("application/json").setBody(body);
        System.out.println(request.getMethod());
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", 405, response.code.intValue());  //BUG za GET poziv vraca 404 umesto 405
        Assertions.assertThat(response.getCodeMessage().contains("Request method "+ method+" not supported"));
    }
}
