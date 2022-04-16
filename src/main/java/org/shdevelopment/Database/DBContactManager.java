package org.shdevelopment.Database;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.columns.StringColumnHandler;
import org.shdevelopment.Structures.Contact;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DBContactManager implements EntityManagerInterface<Contact> {

    private static DBContactManager dbContactManager;

    public static DBContactManager getDbContactManager(){
        if(dbContactManager == null)
            dbContactManager = new DBContactManager();
        return dbContactManager;
    }

    @Override
    public void createEntity(Contact contact) {
        String create = "INSERT INTO bookmark VALUES (?,?)";

        try {
            PreparedStatement preparedStatement = Driver.getConnection().prepareStatement(create);
            preparedStatement.setString(1, contact.getIp());
            preparedStatement.setString(2, contact.getName());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Contact readEntity(long id) {
        return null;
    }

    public Contact readEntity(String ip) throws SQLException {
        Connection connection = Driver.getConnection();
        BeanHandler<Contact> beanHandler = new BeanHandler<>(Contact.class);
        QueryRunner runner = new QueryRunner();
        return runner.query(connection,"SELECT * FROM bookmark WHERE ip=?", beanHandler, ip);
    }

    public List<Contact> readAllContacts() throws SQLException {
        Connection connection = Driver.getConnection();
        BeanListHandler<Contact> beanListHandler = new BeanListHandler<>(Contact.class);
        QueryRunner runner = new QueryRunner();
        return runner.query(connection, "SELECT * FROM bookmark", beanListHandler);
    }

    @Override
    public void updateEntity(Contact contact) {

    }

    @Override
    public void deleteEntity(Contact contact) {

    }
}
