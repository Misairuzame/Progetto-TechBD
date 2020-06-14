package com.gb.db.PostgreSQLImpl;

import com.gb.DAO.MusicDAO;
import com.gb.modelObject.Music;
import com.gb.modelObject.SearchFilter;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.gb.Constants.*;

public class PostgreSQLImpl implements MusicDAO {

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
            BufferedReader br = new BufferedReader(new FileReader("creds.txt"));
            String usr = br.readLine();
            String psw = br.readLine();
            br.close();
            PGSimpleDataSource dataSource = new PGSimpleDataSource();
            dataSource.setServerNames(new String[]{"localhost"});
            dataSource.setDatabaseName("MusicDBPostgres");
            dataSource.setPortNumbers(new int[]{5432});
            dataSource.setUser(usr);
            dataSource.setPassword(psw);
            dataSource.setCurrentSchema("MusicDB");
            conn = dataSource.getConnection();
            logger.info("Database connection created successfully.");

            String sql = "SET SCHEMA '"+DB_NAME+"'";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.executeUpdate();
            ps.close();
            logger.info("Schema "+ DB_NAME +" set successfully.");

        } catch (SQLException | IOException e) {
            logger.error("Exception during PostgreSQLImpl constructor: " + e.getMessage());
            conn = null;
        }

    }

    public static void main(String[] args) {
        logger.debug("Connection test");
        PostgreSQLImpl test = new PostgreSQLImpl();
    }

    @Override
    public List<Music> getAllMusic(int page) {

        List<Music> musicList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM " + MUSIC_TABLE +
                " LIMIT ? OFFSET ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1,PAGE_SIZE);
            ps.setInt(2,page*PAGE_SIZE);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    musicList.add(new Music(rs));
                }
            }
            return musicList;
        } catch (SQLException e) {
            logger.error("Error in getAllMusic: {}", e.getMessage());
            return null;
        }

    }

    @Override
    public List<Music> getMusicById(long id) {
        return null;
    }

    @Override
    public List<Music> getMusicJoinAll(long id) {
        return null;
    }

    @Override
    public List<Music> getMusicByParams(SearchFilter filter, int page) {
        return null;
    }

    @Override
    public int updateOneMusic(Music music) {
        return 0;
    }

    @Override
    public int addOneMusic(Music music) {
        return 0;
    }

    @Override
    public int deleteOneMusic(long id) {
        return 0;
    }
}
