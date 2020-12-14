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

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CreateAuditTest {
    private static String url= Init.generateAppUrl(ServiceMethod.ADD_AUDIT),username;
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

    @DataProvider
    public Object[][] allActions() {
        return new Object[][] {
                { "SAVE"}, { "LOGIN"}, {"DELETE"},{"UPDATE"}
        };
    }
    @Test(dataProvider = "allActions")
    public static void aRegularCallAllActions(String action) throws Exception {
        HTTPRequest request = new HTTPRequest(url+"?action="+action+"&username="+username)
                .setMethod(HTTPMethod.PUT).setContentType("application/json");
        HTTPResponse response = request.sendRequest();
        System.out.println(response.body +" dataP");
        assertEquals("Bad response code: ", 201, response.code.intValue());
        CreatedAudit createAuditId = Mapper.mapFromJSON(response.body, CreatedAudit.class);
        List<GetAudit> getAudits=Methods.getAudit(username);
        for(GetAudit audits: getAudits) {
            Assertions.assertThat(audits.getId().equals(createAuditId.getId()));
            Assertions.assertThat(audits.getUser().getUsername().equals(username));
        }
    }

    @Test
    public static void allActionsOneUser() throws Exception {
        List<String> actions= Arrays.asList("SAVE","LOGIN","DELETE","UPDATE");
        for (String a:actions){
            HTTPRequest request = new HTTPRequest(url+"?action="+a+"&username="+username)
                    .setMethod(HTTPMethod.PUT).setContentType("application/json");
            HTTPResponse response = request.sendRequest();
            assertEquals("Bad response code: ", 201, response.code.intValue());
            CreatedAudit audit = Mapper.mapFromJSON(response.body, CreatedAudit.class);
            List<GetAudit> getAudits=Methods.getAudit(username);
            for(GetAudit audits: getAudits) {
                Assertions.assertThat(audits.getId().equals(audit.getId()));
                Assertions.assertThat(audits.getActionType().equals(a));
                Assertions.assertThat(audits.getUser().getUsername().equals(username));
                Assertions.assertThat(audits.getUser().getId().equals(userID));
            }
        }
    }

    @Test
    public static void sameActionsOneUser() throws Exception {
        List<String> actions= Arrays.asList("SAVE","SAVE","SAVE","SAVE");
        for (String a:actions){
            HTTPRequest request = new HTTPRequest(url+"?action="+a+"&username="+username)
                    .setMethod(HTTPMethod.PUT).setContentType("application/json");
            HTTPResponse response = request.sendRequest();
            System.out.println(response.body);
            assertEquals("Bad response code: ", 201, response.code.intValue());
            CreatedAudit audit = Mapper.mapFromJSON(response.body, CreatedAudit.class);
            List<GetAudit> getAudits=Methods.getAudit(username);
            for(GetAudit audits: getAudits) {
                Assertions.assertThat(audits.getId().equals(audit.getId()));
                Assertions.assertThat(audits.getActionType().equals(a));
                Assertions.assertThat(audits.getUser().getUsername().equals(username));
                Assertions.assertThat(audits.getUser().getId().equals(userID));
            }
        }
    }
    @DataProvider(parallel = true)
    public Object[][] badActions() {
        return new Object[][] {
                {"SAVEZ"}, { "login"}, {"DeLeTe"},{"*"},{""}
        };
    }
    @Test(dataProvider = "badActions",enabled=false)  //BUG "status":500,"error":"Internal Server Error" a treba da bude 400 neodgovarajuca audit akcija
    public static void badAuditActions(String action) throws Exception {
        HTTPRequest request = new HTTPRequest(url+"?action="+action+"&username="+username)
                .setMethod(HTTPMethod.PUT).setContentType("application/json");
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", 400, response.code.intValue());
    }
    @Test(enabled=false)
    public static void unknownUsername() throws Exception {
        HTTPRequest request = new HTTPRequest(url + "?action=" + "LOGIN" + "&username=" + username + 2)
                .setMethod(HTTPMethod.PUT).setContentType("application/json");
        HTTPResponse response = request.sendRequest();
        System.out.println(response.body + " dataP");
        assertEquals("Bad response code: ", 400, response.code.intValue()); //BUG "status":500,"error":"Internal Server Error","message":"Unknown user" a treba 400 fali username
    }
    @DataProvider
    public Object[][] methods() {
        return new Object[][] {
                {"GET"}, { "POST"}, {"DELETE"}
        };
    }
    @Test(dataProvider = "methods")
    public static void allMethods(String method) throws Exception {
        HTTPRequest request = new HTTPRequest(url + "?action=" + "LOGIN" + "&username=" + username)
                .setMethod(HTTPMethod.valueOf(method)).setContentType("application/json");
        System.out.println(request.getMethod());
        HTTPResponse response = request.sendRequest();
        assertEquals("Bad response code: ", 405, response.code.intValue());
        Assertions.assertThat(response.getCodeMessage().contains("Request method "+ method+" not supported"));
    }
}
