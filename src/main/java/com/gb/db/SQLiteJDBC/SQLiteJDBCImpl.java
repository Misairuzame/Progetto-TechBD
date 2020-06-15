package com.gb.db.SQLiteJDBC;

import com.gb.modelObject.Music;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.gb.Constants.*;

public class SQLiteJDBCImpl {

    private static Connection conn = null;
    private static SQLiteJDBCImpl Database = null;
    private static Logger logger = LoggerFactory.getLogger(SQLiteJDBCImpl.class);

    private SQLiteJDBCImpl() {
/*
        try {
            conn = DriverManager.getConnection(DB_PATH);
            logger.info("Database connection created successfully.");

            String sql =
                    " CREATE TABLE IF NOT EXISTS "
                    + TABLE_NAME+ " ("
                    + ID        + " INTEGER PRIMARY KEY, "
                    + TITLE     + " TEXT NOT NULL, "
                    + AUTHOR    + " TEXT NOT NULL, "
                    + ALBUM     + " TEXT NOT NULL, "
                    + YEAR      + " INTEGER NOT NULL, "
                    + GENRE     + " TEXT NOT NULL, "
                    + URL       + " TEXT NOT NULL )";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error("Exception during SQLiteJDBCImpl constructor: " + e.getMessage());
            conn = null;
        }


 */
    }

    public static synchronized SQLiteJDBCImpl getInstance() {

        if(Database == null) {
            Database = new SQLiteJDBCImpl();
            if (conn == null) {
                return null;
            }
        }
        return Database;

    }

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

    public List<Music> getMusicById(long id) {

        List<Music> musicList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM "  + MUSIC_TABLE +
                " WHERE " + M_ID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    musicList.add(new Music(rs));
                }
            }
            return musicList;
        } catch (SQLException e) {
            logger.error("Error in getMusicById: {}", e.getMessage());
            return null;
        }

    }

    public int addOneMusic(Music music) {
        /*
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + TABLE_NAME +
                " WHERE " + ID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {

            pStat.setLong(1, music.getMusicId());

            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (exists) {
                    logger.info("Esiste gia' una canzone con id {}, impossibile crearne una nuova.", music.getMusicId());
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in insertMusic: " + e.getMessage());
            return -2;
        }


        String sql =
                " INSERT INTO " + TABLE_NAME +
                " ( " + ID + ", " + TITLE + ", " + AUTHOR + ", " + ALBUM + ", " +
                YEAR + ", " + GENRE + ", " + URL + " ) " + " VALUES " +
                "(?,?,?,?,?,?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, music.getMusicId());
            ps.setString(2, music.getTitle());
            ps.setString(3, music.getAuthor());
            ps.setString(4, music.getAlbum());
            ps.setInt(5, music.getYear());
            ps.setString(6, music.getGenre());
            ps.setString(7, music.getUrl());

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception in insertMusic: " + e.getMessage());
            return -2;
        }

         */

        return 0;
    }

    public int addManyMusic(List<Music> musicList) {

        try{
            conn.setAutoCommit(false);
            for(Music music : musicList) {
                if(addOneMusic(music) < 0) {
                    throw new SQLException("Errore in addManyMusic interno a insertMusic.");
                }
            }
            conn.commit();
        } catch(SQLException e) {
            logger.error("Exception in addManyMusic: " + e.getMessage());
            try {
                conn.rollback();
            } catch(SQLException ex) {
                logger.error("Exception in addManyMusic: " + ex.getMessage());
            }
            return -1;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                logger.error("Exception in addManyMusic: " + ex.getMessage());
            }
        }
        return 0;
    }

    public int updateOneMusic(Music music) {
/*
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + TABLE_NAME +
                " WHERE " + ID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {

            pStat.setLong(1, music.getMusicId());

            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (!exists) {
                    logger.info("La canzone con id {} non esiste, impossibile aggiornarla.", music.getMusicId());
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in updateMusic: " + e.getMessage());
            return -2;
        }

        String sql =
                " UPDATE " + TABLE_NAME + " SET " +
                TITLE + " = ?, " + AUTHOR + " = ?, " + ALBUM + " = ?, " +
                YEAR + " = ?, "  + GENRE  + " = ?, " + URL   + " = ? "  +
                " WHERE " + ID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, music.getTitle());
            ps.setString(2, music.getAuthor());
            ps.setString(3, music.getAlbum());
            ps.setInt(4, music.getYear());
            ps.setString(5, music.getGenre());
            ps.setString(6, music.getUrl());
            ps.setLong(7, music.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception in updateMusic: " + e.getMessage());
            return -2;
        }

 */

        return 0;
    }

    public int deleteOneMusic(long id) {

        String check =
                " SELECT COUNT(*) " +
                " FROM "  + MUSIC_TABLE +
                " WHERE " + M_ID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {

            pStat.setLong(1, id);

            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (!exists) {
                    logger.info("La canzone con id {} non esiste, impossibile eliminarla.", id);
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in updateMusic: " + e.getMessage());
            return -2;
        }

        String sql =
                " DELETE FROM " + MUSIC_TABLE +
                " WHERE " + M_ID  + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
            return 0;
        } catch (SQLException e) {
            logger.error("Error in DeleteOneMusic: {}", e.getMessage());
            return -1;
        }
    }

}
