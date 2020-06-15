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
                " WHERE " + MUSICID + " = ? ";

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
                " WHERE " + MUSICID + " = ? ";

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
