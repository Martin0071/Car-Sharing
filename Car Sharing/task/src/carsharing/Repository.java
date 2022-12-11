package carsharing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

class Repository {
    private static final String CREATE_TABLE_QUERY = """
            CREATE TABLE IF NOT EXISTS COMPANY(
            ID INTEGER PRIMARY KEY AUTO_INCREMENT,
            NAME VARCHAR UNIQUE NOT NULL
            );
            """;
    private static final String SQL_GET_ALL = "SELECT * FROM COMPANY";
    private static final String SQL_ADD_COMPANY = "INSERT INTO COMPANY (NAME) VALUES (?);";

    Connection conn;

    public Repository(Connection conn) {
        this.conn = conn;
        try (var ps = conn.prepareStatement(CREATE_TABLE_QUERY)) {
            ps.executeUpdate();
            ps.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    List<String> getAll() {
        List<String> companies = new ArrayList<>();
        try {
            var ps = conn.prepareStatement(SQL_GET_ALL);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                companies.add(rs.getString("NAME"));
            }
            rs.close();
            ps.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return companies;
    }

    void setSqlAddCompany(String name) {
        try {
            var ps = conn.prepareStatement(SQL_ADD_COMPANY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.executeUpdate();
            ps.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
