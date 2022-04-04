package org.shdevelopment.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Driver {

    private static Connection connection;

    static {
        String url = "jdbc:h2:~/h2/test";
        String user = "sweethomeapp";
        String pass = "h2dbswthm";

        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection(url, user, pass);
            connection.setAutoCommit(true);
        }
        catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}