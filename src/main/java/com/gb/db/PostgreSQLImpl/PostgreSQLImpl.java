package com.gb.db.PostgreSQLImpl;

import com.gb.DAO.*;
import com.gb.modelObject.*;
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

public class PostgreSQLImpl implements MusicDAO, AlbumDAO, ArtistDAO, GroupDAO, GenreDAO, LinkDAO {

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

    @Override
    /* Done */
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
    /* Done */
    public List<Music> getMusicById(int musicId) {
        List<Music> musicList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM "  + MUSIC_TABLE +
                " WHERE " + MUSICID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, musicId);
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

    @Override
    public Object getMusicJoinAll(int id) {
        return null;
    }

    @Override
    /* Done */
    public int updateMusic(Music music) {
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + MUSIC_TABLE +
                " WHERE " + MUSICID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {
            pStat.setInt(1, music.getMusicId());
            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (!exists) {
                    logger.warn("La canzone con id {} non esiste, impossibile aggiornarla.", music.getMusicId());
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in updateMusic: " + e.getMessage());
            return -2;
        }

        String sql =
                " UPDATE " + MUSIC_TABLE + " SET " +
                 TITLE + " = ?, " + AUTHORID + " = ?, " + ALBUMID + " = ?, " +
                 YEAR + " = ?, "  + GENREID  + " = ? " +
                " WHERE " + MUSICID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, music.getTitle());
            ps.setInt(2, music.getAuthorId());
            ps.setInt(3, music.getAlbumId());
            ps.setInt(4, music.getYear());
            ps.setInt(5, music.getGenreId());
            ps.setLong(6, music.getMusicId());

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception in updateMusic: " + e.getMessage());
            return -2;
        }

        return 0;
    }

    @Override
    /* Done */
    public int insertMusic(Music music) {
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + MUSIC_TABLE +
                " WHERE " + MUSICID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {
            pStat.setInt(1, music.getMusicId());
            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (exists) {
                    logger.warn("Esiste gia' una canzone con id {}, impossibile crearne una nuova.", music.getMusicId());
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in insertMusic: " + e.getMessage());
            return -2;
        }

        String sql =
                " INSERT INTO " + MUSIC_TABLE +
                " ( " + MUSICID + ", " + TITLE + ", " + AUTHORID + ", " + ALBUMID + ", " +
                 YEAR + ", " + GENREID + " ) VALUES (?,?,?,?,?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, music.getMusicId());
            ps.setString(2, music.getTitle());
            ps.setInt(3, music.getAuthorId());
            ps.setInt(4, music.getAlbumId());
            ps.setInt(5, music.getAuthorId());
            ps.setInt(6, music.getYear());
            ps.setInt(7, music.getGenreId());

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception in insertMusic: " + e.getMessage());
            return -2;
        }

        return 0;
    }

    @Override
    /* Done */
    public int deleteMusic(int id) {
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + MUSIC_TABLE +
                " WHERE " + MUSICID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {
            pStat.setInt(1, id);
            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (!exists) {
                    logger.warn("La canzone con id {} non esiste, impossibile eliminarla.", id);
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in deleteMusic: " + e.getMessage());
            return -2;
        }

        String sql =
                " DELETE FROM " + MUSIC_TABLE +
                " WHERE " + MUSICID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error in deleteMusic: {}", e.getMessage());
            return -2;
        }

        return 0;
    }

    @Override
    /* Done */
    public List<Album> getAllAlbums() {
        List<Album> albumList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM "  + ALBUM_TABLE;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    albumList.add(new Album(rs));
                }
            }
            return albumList;
        } catch (SQLException e) {
            logger.error("Error in getAllAlbums: {}", e.getMessage());
            return null;
        }
    }

    @Override
    /* Done */
    public List<Album> getAlbumById(int albumId) {
        List<Album> albumList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM "  + ALBUM_TABLE +
                " WHERE " + ALBUMID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, albumId);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    albumList.add(new Album(rs));
                }
            }
            return albumList;
        } catch (SQLException e) {
            logger.error("Error in getAlbumById: {}", e.getMessage());
            return null;
        }
    }

    @Override
    /* Done */
    public int deleteAlbum(int albumId) {
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + ALBUM_TABLE +
                " WHERE " + ALBUMID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {
            pStat.setInt(1, albumId);
            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (!exists) {
                    logger.warn("L'album con id {} non esiste, impossibile eliminarla.", albumId);
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in deleteAlbum: " + e.getMessage());
            return -2;
        }

        String sql =
                " DELETE FROM " + ALBUM_TABLE +
                " WHERE " + ALBUMID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, albumId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error in deleteAlbum: {}", e.getMessage());
            return -2;
        }

        return 0;
    }

    @Override
    /* Done */
    public int insertAlbum(Album album) {
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + ALBUM_TABLE +
                " WHERE " + ALBUMID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {
            pStat.setInt(1, album.getAlbumId());
            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (exists) {
                    logger.warn("Esiste gia' un album con id {}, impossibile crearne uno nuovo.", album.getAlbumId());
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in insertAlbum: " + e.getMessage());
            return -2;
        }

        String sql =
                " INSERT INTO " + ALBUM_TABLE +
                " ( " + ALBUMID + ", " + TITLE + ", " + YEAR + ", " + GROUPID +
                " ) VALUES (?,?,?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, album.getAlbumId());
            ps.setString(2, album.getTitle());
            ps.setInt(3, album.getYear());
            ps.setInt(4, album.getGroupId());

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception in insertAlbum: " + e.getMessage());
            return -2;
        }

        return 0;
    }

    @Override
    public Object albumJoinGroup() {
        return null;
    }

    @Override
    /* Done */
    public List<Artist> getAllArtists() {
        List<Artist> artistList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM "  + ARTIST_TABLE;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    artistList.add(new Artist(rs));
                }
            }
            return artistList;
        } catch (SQLException e) {
            logger.error("Error in getAllArtists: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Object artistJoinGroup() {
        return null;
    }

    @Override
    /* Done */
    public int updateArtist(Artist artist) {
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + ARTIST_TABLE +
                " WHERE " + ARTISTID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {
            pStat.setInt(1, artist.getArtistId());
            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (!exists) {
                    logger.warn("L'artista con id {} non esiste, impossibile aggiornarlo.", artist.getArtistId());
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in updateArtist: " + e.getMessage());
            return -2;
        }

        String sql =
                " UPDATE " + ARTIST_TABLE + " SET " +
                 NAME + " = ?, " + GROUPID + " = ? " +
                " WHERE " + ARTISTID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, artist.getName());
            ps.setInt(2, artist.getGroupId());
            ps.setInt(3, artist.getArtistId());

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception in updateArtist: " + e.getMessage());
            return -2;
        }

        return 0;
    }

    @Override
    /* Done */
    public List<Genre> getAllGenres() {
        List<Genre> genreList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM "  + GENRE_TABLE;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    genreList.add(new Genre(rs));
                }
            }
            return genreList;
        } catch (SQLException e) {
            logger.error("Error in getAllGenres: {}", e.getMessage());
            return null;
        }
    }

    @Override
    /* Done */
    public List<Genre> getGenreById(int genreId) {
        List<Genre> genreList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM "  + GENRE_TABLE +
                " WHERE " + GENREID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, genreId);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    genreList.add(new Genre(rs));
                }
            }
            return genreList;
        } catch (SQLException e) {
            logger.error("Error in getGenreById: {}", e.getMessage());
            return null;
        }
    }

    @Override
    /* Done */
    public int insertGenre(Genre genre) {
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + GENRE_TABLE +
                " WHERE " + GENREID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {
            pStat.setInt(1, genre.getGenreId());
            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (exists) {
                    logger.warn("Esiste gia' un genere con id {}, impossibile crearne uno nuovo.", genre.getGenreId());
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in insertGenre: " + e.getMessage());
            return -2;
        }

        String sql =
                " INSERT INTO " + GENRE_TABLE +
                " ( " + GENREID + ", " + NAME +
                " ) VALUES (?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, genre.getGenreId());
            ps.setString(2, genre.getName());

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception in insertGenre: " + e.getMessage());
            return -2;
        }

        return 0;
    }

    @Override
    /* Done */
    public List<Group> getAllGroups() {
        List<Group> groupList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM "  + GROUP_TABLE;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    groupList.add(new Group(rs));
                }
            }
            return groupList;
        } catch (SQLException e) {
            logger.error("Error in getAllGroups: {}", e.getMessage());
            return null;
        }
    }

    @Override
    /* Done */
    public List<Group> getGroupById(int groupId) {
        List<Group> groupList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM "  + GROUP_TABLE +
                " WHERE " + GROUPID + " = ? ";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    groupList.add(new Group(rs));
                }
            }
            return groupList;
        } catch (SQLException e) {
            logger.error("Error in getGroupById: {}", e.getMessage());
            return null;
        }
    }

    @Override
    /* Done */
    public int insertGroup(Group group) {
        String check =
                " SELECT COUNT(*) " +
                " FROM "  + GROUP_TABLE +
                " WHERE " + GROUPID + " = ? ";

        boolean exists = false;

        try (PreparedStatement pStat = conn.prepareStatement(check)) {
            pStat.setInt(1, group.getGroupId());
            try (ResultSet rs = pStat.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                if (exists) {
                    logger.warn("Esiste gia' un gruppo con id {}, impossibile crearne uno nuovo.", group.getGroupId());
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in insertGroup: " + e.getMessage());
            return -2;
        }

        String sql =
                " INSERT INTO " + GROUP_TABLE +
                " ( " + GROUPID + ", " + NAME +
                " ) VALUES (?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, group.getGroupId());
            ps.setString(2, group.getName());

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception in insertGroup: " + e.getMessage());
            return -2;
        }

        return 0;
    }

    @Override
    /* Done */
    public List<Link> getAllLinks() {
        List<Link> linkList = new ArrayList<>();

        String sql =
                " SELECT * " +
                " FROM "  + LINK_TABLE;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    linkList.add(new Link(rs));
                }
            }
            return linkList;
        } catch (SQLException e) {
            logger.error("Error in getAllLinks: {}", e.getMessage());
            return null;
        }
    }

    public List<MusicJoinLink> musicJoinLink() {
        List<MusicJoinLink> musicList = new ArrayList<>();

        String sql =
                " SELECT M."+MUSICID+", M."+TITLE+", M."+AUTHORID+", M."+ALBUMID+", M."+YEAR+", M."+GENREID+", L."+LINK+
                " FROM "+MUSIC_TABLE+" as M LEFT JOIN "+LINK+" as L" +
                " ON M."+MUSICID+" = L."+MUSICID;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    musicList.add(new MusicJoinLink(rs));
                }
            }
            return musicList;
        } catch (SQLException e) {
            logger.error("Error in musicJoinLink: {}", e.getMessage());
            return null;
        }
    }

}
