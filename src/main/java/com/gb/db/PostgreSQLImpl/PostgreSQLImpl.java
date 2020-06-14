package com.gb.db.PostgreSQLImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.gb.Constants.*;

public class PostgreSQLImpl {

    public static Connection conn = null;
    public static PostgreSQLImpl Database = null;
    private static final Logger logger = LoggerFactory.getLogger(PostgreSQLImpl.class);

    public static synchronized PostgreSQLImpl getInstance() {

        if(Database == null) {
            Database = new PostgreSQLImpl();
            if (conn == null) {
                return null;
            }
        }
        return Database;

    }

    private PostgreSQLImpl() {

        try {
            String psw = new BufferedReader(new FileReader("creds.txt")).readLine();
            conn = DriverManager.getConnection(DB_PATH, "postgres", psw);
            logger.info("Database connection created successfully.");

        } catch (SQLException | IOException e) {
            logger.error("Exception during PostgreSQLImpl constructor: " + e.getMessage());
            conn = null;
        }

    }

    public static void main(String[] args) {
        logger.debug("Test");
        PostgreSQLImpl test = new PostgreSQLImpl();
    }

}
