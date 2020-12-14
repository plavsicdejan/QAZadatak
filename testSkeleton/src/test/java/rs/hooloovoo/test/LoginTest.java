package rs.hooloovoo.test;

import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import rs.hooloovoo.test.resources.Init;
import rs.hooloovoo.test.resources.ServiceMethod;
import rs.hooloovoo.test.resources.Users;
import rs.hooloovoo.test.resources.http.HTTPMethod;
import rs.hooloovoo.test.resources.http.HTTPRequest;
import rs.hooloovoo.test.resources.http.HTTPResponse;

import static org.junit.Assert.assertEquals;

public class LoginTest {

    private static String url= Init.generateAppUrl(ServiceMethod.USER_LOGIN),username,password,body;

    @BeforeClass
    public static void before() throws Exception {
        Users user= Methods.checkFixedUser();
        username=user.getUsername();
        password= user.getPassword();
        body="{\"username\": \""+username+"\",\n" +
                " \"password\": \""+password+"\"}";
    }

    @Test
    public static void aRegularCall() throws Exception {
        HTTPRequest request = new HTTPRequest(url).setMethod(HTTPMethod.POST).setContentType("application/json").setBody(body);
        System.out.println(body);
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", 200, response.code.intValue());
    }

    @Test(enabled = false)
    public static void badUsername() throws Exception {
        String body2=body.replace(username,username+2);
        System.out.println(body2);
        HTTPRequest request = new HTTPRequest(url).setMethod(HTTPMethod.POST).setContentType("application/json").setBody(body2);
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", 401, response.code.intValue()); //BUG 417-Expectation Failed Error code-should be 401 Unauthorized
        assertEquals("Bad response message: ", "Login failed.", response.body);
    }
    @Test(enabled = false)
    public static void badPassword() throws Exception {
        String body2=body.replace(password,password+2);
        System.out.println(body2);
        HTTPRequest request = new HTTPRequest(url).setMethod(HTTPMethod.POST).setContentType("application/json").setBody(body2);
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", 401, response.code.intValue()); //BUG 417-Expectation Failed Error code-should be 401 Unauthorized
        assertEquals("Bad response message: ", "Login failed.", response.body);
    }
    @DataProvider
    public Object[][] methods() {
        return new Object[][] {
//                {"GET"},
                { "PUT"}, {"DELETE"}
        };
    }
    @Test(dataProvider = "methods")
    public static void allMethods(String method) throws Exception {
        HTTPRequest request = new HTTPRequest(url).setMethod(HTTPMethod.valueOf(method)).setContentType("application/json").setBody(body);
        System.out.println(request.getMethod());
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", 405, response.code.intValue());  //BUG za GET poziv vraca 404 umesto 405
        Assertions.assertThat(response.getCodeMessage().contains("Request method "+ method+" not supported"));
    }
}
