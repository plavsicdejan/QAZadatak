package rs.hooloovoo.test.resources;

import org.testng.Assert;

import java.sql.*;
import java.util.List;

public class JDBC_Utils {
    private static final String deleteUser = "delete from users where id in ";
    private static final String deleteAudit = "delete from AUDIT_LOG_ENTRIES where user_id in ";
    private static final String checkUser = "select id from users where id in ";
    private static final String checkAudit = "select user_id from AUDIT_LOG_ENTRIES where user_id in ";

    public static Connection getConnectionDB() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(
                    "jdbc:h2:tcp://localhost:8762/mem:userdb?IFEXISTS=false",
                    "sa",
                    "password");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void closeConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteUserFromDB(String ids) {
        PreparedStatement stmt;
        Connection conn = getConnectionDB();
        try {
            stmt = conn.prepareStatement(deleteUser.concat("(" + ids + ")"));
            stmt.executeUpdate();

            //////check existance
            stmt = conn.prepareStatement(checkUser.concat("(" + ids + ")"));
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                //Retrieve by column name
                String i_d  = rs.getString("id");
                //Display values
                Assert.assertNull("Customer was not deleted",i_d);
            }
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection(conn);
    }
    public static void deleteAuditFromDB(String ids) {
        PreparedStatement stmt;
        Connection conn = getConnectionDB();
        try {
            stmt = conn.prepareStatement(deleteAudit.concat("(" + ids + ")"));
            stmt.executeUpdate();

            //////check existance
            stmt = conn.prepareStatement(checkAudit.concat("(" + ids + ")"));
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                //Retrieve by column name
                String i_d  = rs.getString("user_id");
                //Display values
                Assert.assertNull("Audit was not deleted",i_d);
            }
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection(conn);
    }

    public static String collectUsers(List<String> usernames) {
        StringBuilder sbUsers = new StringBuilder();
        for (int i = 0; i < usernames.size(); i++) {
            sbUsers.append("'" + usernames.get(i) + "',");
        }
        sbUsers.deleteCharAt(sbUsers.length() - 1);
        return sbUsers.toString();
    }
}
