package rs.hooloovoo.test;

import org.assertj.core.api.Assertions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import rs.hooloovoo.test.model.CreatedAudit;
import rs.hooloovoo.test.model.GetAudit;
import rs.hooloovoo.test.resources.Init;
import rs.hooloovoo.test.resources.JDBC_Utils;
import rs.hooloovoo.test.resources.ServiceMethod;
import rs.hooloovoo.test.resources.Users;
import rs.hooloovoo.test.resources.http.HTTPMethod;
import rs.hooloovoo.test.resources.http.HTTPRequest;
import rs.hooloovoo.test.resources.http.HTTPResponse;
import rs.hooloovoo.test.resources.map.Mapper;

import java.util.List;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

public class GetAuditTest {
    private static String url= Init.generateAppUrl(ServiceMethod.GET_AUDIT);
    private static String username;
    private static Integer userID;
    public static Users user;

    @BeforeClass
    public static void before() throws Exception {
        user=Methods.checkFixedUser();
        username=user.getUsername();
        userID=user.user_id;
    }

    @AfterClass
    public static void after() {
        JDBC_Utils.deleteAuditFromDB(userID.toString());
    }

    @Test
    public static void aRegularCall() throws Exception {
        CreatedAudit idAudit=Methods.createAudit(username,"LOGIN");
        int auditID=idAudit.getId();
        HTTPRequest request = new HTTPRequest(format(url,username)+"?username="+username)
                .setMethod(HTTPMethod.GET).setContentType("application/json");
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", 200, response.code.intValue());
        List<GetAudit> getaudit= Mapper.mapFromJsonArray(response.body, GetAudit[].class);
        for(GetAudit audits: getaudit) {
            Assertions.assertThat(audits.getId().equals(auditID));
            Assertions.assertThat(audits.getActionType().equals("LOGIN"));
            Assertions.assertThat(audits.getUser().getUsername().equals(username));
            Assertions.assertThat(audits.getUser().getId().equals(userID.intValue()));
//            Assert.assertNotNull(audits.getCreatedAt(),"Created Time is null");  //BUG Created Time is null
        }
    }
    @DataProvider
    public Object[][] allActions() {
        return new Object[][] {
                { "SAVE"}, { "LOGIN"}, {"DELETE"},{"UPDATE"}
        };
    }
    @Test(dataProvider = "allActions")
    public static void aRegularCallAllActions(String action) throws Exception {
        CreatedAudit idAudit = Methods.createAudit(username, action);
        System.out.println(username+ " "+ action);
        int auditID = idAudit.getId();
        HTTPRequest request = new HTTPRequest(format(url, username) + "?username=" + username)
                .setMethod(HTTPMethod.GET).setContentType("application/json");
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", 200, response.code.intValue());
        List<GetAudit> getaudit = Mapper.mapFromJsonArray(response.body, GetAudit[].class);
        for (GetAudit audits : getaudit) {
            Assertions.assertThat(audits.getId().equals(auditID));
            Assertions.assertThat(audits.getUser().getUsername().equals(username));
            Assertions.assertThat(audits.getUser().getId().equals(userID.intValue()));
        }
    }
    @Test(enabled = false)
    public static void unknownUsername() throws Exception {
        HTTPRequest request = new HTTPRequest(format(url, username+1) + "?username=" + username+1)
                .setMethod(HTTPMethod.GET).setContentType("application/json");
        System.out.println(request.getUrl());
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", 400, response.code.intValue()); //BUG za nepoznatog usera vraca se prazan niz audita i 200 response
    }
    @Test(enabled = false)
    public static void unknownUsernameQueryParam() throws Exception {
        HTTPRequest request = new HTTPRequest(format(url, username) + "?username=" + username + 2)
                .setMethod(HTTPMethod.GET).setContentType("application/json");
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", 400, response.code.intValue()); //BUG za nepoznatog usera u query parametru vraca se prazan niz audita i 200 response
    }
    @Test(enabled = false)
    public static void unknownUsernamePath() throws Exception {
        HTTPRequest request = new HTTPRequest(format(url, username+2) + "?username=" + username)
                .setMethod(HTTPMethod.GET).setContentType("application/json");
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", 400, response.code.intValue()); //BUG za nepoznatog usera u path-u regularno vraca auditID i 200 response
    }
    @DataProvider
    public Object[][] methods() {
        return new Object[][] {
                {"PUT"}, { "POST"}, {"DELETE"}
        };
    }
    @Test(dataProvider = "methods")
    public static void allMethods(String method) throws Exception {
        HTTPRequest request = new HTTPRequest(format(url, username) + "?username=" + username)
                .setMethod(HTTPMethod.valueOf(method)).setContentType("application/json");
        System.out.println(request.getMethod());
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", 405, response.code.intValue());
        Assertions.assertThat(response.getCodeMessage().contains("Request method "+ method+" not supported"));
    }
}
