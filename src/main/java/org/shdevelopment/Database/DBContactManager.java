package org.shdevelopment.Database;

import org.shdevelopment.Structures.Contact;

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

    public List<Contact> readAllContacts(){
        String query = "SELECT * FROM bookmark";
        return null;
    }

    @Override
    public void updateEntity(Contact contact) {

    }

    @Override
    public void deleteEntity(Contact contact) {

    }
}
