package com.mk;

import com.mk.parsers.AliExpressParser;

import java.sql.*;

public class DBConnector {

    private static final String DB_PATH = "jdbc:sqlite:src/main/resources/db.db";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_URL = "url";
    private Connection connection;

    public void insertItem(String name, String url) {
        String sql = "INSERT INTO Items(name,url) VALUES(?,?)";
        try {
            this.connection = DriverManager.getConnection(DB_PATH);
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, url);
            preparedStatement.executeUpdate();
            this.connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteItemByUrl(String url) {
        String sql = "DELETE FROM Items WHERE url = ?";
        try {
            this.connection = DriverManager.getConnection(DB_PATH);
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, url);
            preparedStatement.executeUpdate();
            this.connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int getItemsCount() {
        String sql = "SELECT id FROM Items";
        int count = 0;
        try {
            this.connection = DriverManager.getConnection(DB_PATH);
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                count++;
            }
            this.connection.close();
            return count;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return count;
        }
    }

    public Item getFirstItem(){
        String sql = "SELECT name, url FROM Items LIMIT 1";
        try {
            this.connection = DriverManager.getConnection(DB_PATH);
            Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            String name = resultSet.getString(COLUMN_NAME);
            String url = resultSet.getString(COLUMN_URL);
            AliExpressParser aliExpressParser = new AliExpressParser(url);
            Item item = aliExpressParser.parseItem();
            item.setName(name);
            this.connection.close();
            return item;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
