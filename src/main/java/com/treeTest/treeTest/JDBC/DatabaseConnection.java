package com.treeTest.treeTest.JDBC;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;


@Configuration
public class DatabaseConnection {
    private final DataSource dataSource;

    public DatabaseConnection(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}