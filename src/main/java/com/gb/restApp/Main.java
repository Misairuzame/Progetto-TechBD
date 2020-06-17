package com.gb.restApp;

import static spark.Spark.*;
import com.gb.db.PostgreSQLImpl.PostgreSQLImpl;
import com.gb.modelObject.*;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import java.io.*;
import java.net.URLDecoder;
import java.util.*;

import static org.apache.http.HttpStatus.*;
import static javax.ws.rs.core.MediaType.*;
import static com.gb.Constants.*;
import static com.gb.utils.UtilFunctions.*;

/**
 * Documentazione per le costanti rappresentanti gli stati HTTP, fornite da Apache HTTP:
 * http://hc.apache.org/httpcomponents-core-ga/httpcore/apidocs/org/apache/http/HttpStatus.html
 *
 * Costanti rappresentanti vari Media Type fornite da JAX-RS:
 * https://docs.oracle.com/javaee/7/api/javax/ws/rs/core/MediaType.html
 */

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static void info(String toLog) {
        logger.info("Returned: {}", toLog);
    }

    private static final ThymeleafTemplateEngine engine = new ThymeleafTemplateEngine();

    public static void main(String[] args) {

        port(8080);

        staticFiles.location("/public");

        before(Main::applyFilters);

        get("/", Main::getHomepage);

        path("/music", () -> {
            get("",  Main::dispatchMusic);

            get(":id", Main::dispatchMusicId);
            get("/:id", Main::dispatchMusicId);
        });

        get("/upmusic", Main::upMusicForm);

        get("/insmusic", Main::insMusicForm);

        get("/delmusic", Main::delMusicForm);

        path("/album", () -> {
            get("",  Main::dispatchAlbum);
        });

        get("/insalbum", Main::insAlbumForm);

        get("/delalbum", Main::delAlbumForm);

        path("/artist", () -> {
            get("",  Main::dispatchArtist);
        });

        get("/upartist", Main::upArtistForm);

        get("/insartist", Main::insArtistForm);

        path("/group", () -> {
            get("",  Main::dispatchGroup);
        });

        get("/insgroup", Main::insGroupForm);

        path("/genre", () -> {
            get("",  Main::dispatchGenre);
        });

        get("/insgenre", Main::insGenreForm);

        path("/link", () -> {
            get("",  Main::getLinks);
        });

        get("/mjoinl", Main::musicJoinLink);

        get("/arjoing", Main::artistJoinGroup);

        get("/joinall", Main::joinAll);

        get("/favicon.ico", Main::favicon);

        notFound(Main::handleNotFound);

        options("/*", Main::allowCORS);

    }

    /**
     * Sezione di utility varie
     */

    private static String allowCORS(Request req, Response res) {
        /**
         * Cross-Origin Resource Sharing
         */
        String accessControlRequestHeaders = req.headers("Access-Control-Request-Headers");
        if (accessControlRequestHeaders != null) {
            res.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
        }

        String accessControlRequestMethod = req.headers("Access-Control-Request-Method");
        if (accessControlRequestMethod != null) {
            res.header("Access-Control-Allow-Methods", accessControlRequestMethod);
        }
        return "OK";
    }

    private static void applyFilters(Request req, Response res) {
        /**
         * Permette il CORS (Cross Origin Resource Sharing).
         * Se non è permesso, il server blocca le richieste
         * del client Angular, che gira sulla porta 4200.
         */
        res.header("Access-Control-Allow-Origin" , "*");
        res.header("Access-Control-Allow-Headers", "*");

        /**
         * Toglie lo slash finale, se presente.
         * Il redirect funziona solamente con richieste GET,
         * motivo per cui viene fatto il "doppio matching"
         * nel metodo main.
         */
        String path = req.pathInfo();
        if (req.requestMethod().equals("GET") && path.endsWith("/") && !path.equals("/")) {
            res.redirect(path.substring(0, path.length() - 1));
        }

        /**
         * Mette il content-type della Response a "text/html"
         */
        res.raw().setContentType(TEXT_HTML);

        res.raw().setCharacterEncoding("UTF-8");

        /**
         * Logga la Request
         */
        StringBuilder text = new StringBuilder();
        String message = "Received request: " + req.requestMethod() + " " + req.url();
        text.append(message);
        if(!req.queryParams().isEmpty()) {
            text.append("?");
            text.append(req.queryString());
        }
        if(req.headers(CONT_TYPE) != null && !req.headers(CONT_TYPE).equals("")) {
            text.append("\n");
            text.append("Request content-type:\n");
            text.append(req.headers(CONT_TYPE));
        }
        if(req.body() != null && !req.body().equals("")) {
            text.append("\n");
            text.append("Request body:\n");
            text.append(req.body());
        }
        logger.info(text.toString());
    }

    private static String returnMessage(Request req, Response res, int httpStatus, String messageType, String messageText) {
        res.status(httpStatus);
        info(messageText);
        Map<String, String> model = new HashMap<>();
        model.put("messagetype", messageType);
        model.put("messagetext", messageText);
        return engine.render(new ModelAndView(model, "message"));
    }

    private static String handleNotFound(Request req, Response res) {
        return returnMessage(req, res, SC_NOT_FOUND, "text-warning",
                "Risorsa o collezione non trovata.");
    }

    private static String handleInternalError(Request req, Response res) {
        return returnMessage(req, res, SC_INTERNAL_SERVER_ERROR, "text-danger",
                "Si e' verificato un errore.");
    }

    private static String handleParseError(Request req, Response res) {
        return returnMessage(req, res, SC_BAD_REQUEST, "text-danger",
                "Errore nella deserializzazione dei parametri inviati.");
    }

    private static String getHomepage(Request req, Response res) {
        res.status(SC_OK);
        String message = "Benvenuto nella ReST API MusicService.";
        info(message);
        Map<String, String> model = new HashMap<>();
        model.put("welcometext", message);
        return engine.render(new ModelAndView(model, "home"));
    }


    /**
     * Sezione per effettuare il dispatching delle richieste.
     * Necessario per gestire i metodi HTTP.
     */


    private static String dispatchMusic(Request req, Response res) {
        String userMethod = req.queryParamOrDefault("method", "GET");
        if(userMethod.equalsIgnoreCase(GET))    return getMusic(req, res);
        if(userMethod.equalsIgnoreCase(PUT))    return updateMusic(req, res);
        if(userMethod.equalsIgnoreCase(POST))   return insertMusic(req, res);
        if(userMethod.equalsIgnoreCase(DELETE)) return deleteMusic(req, res);

        return returnMessage(req, res, SC_BAD_REQUEST, "text-danger",
                "Metodo HTTP non supportato.");
    }

    private static String dispatchMusicId(Request req, Response res) {
        String userMethod = req.queryParamOrDefault("method", "GET");
        if(userMethod.equalsIgnoreCase(GET))    return getMusicById(req, res);

        return returnMessage(req, res, SC_BAD_REQUEST, "text-danger",
                "Metodo HTTP non supportato.");
    }

    private static String dispatchGroup(Request req, Response res) {
        String userMethod = req.queryParamOrDefault("method", "GET");
        if(userMethod.equalsIgnoreCase(GET))    return getGroups(req, res);
        if(userMethod.equalsIgnoreCase(POST))   return insertGroup(req, res);

        return returnMessage(req, res, SC_BAD_REQUEST, "text-danger",
                "Metodo HTTP non supportato.");
    }

    private static String dispatchGenre(Request req, Response res) {
        String userMethod = req.queryParamOrDefault("method", "GET");
        if(userMethod.equalsIgnoreCase(GET))    return getGenres(req, res);
        if(userMethod.equalsIgnoreCase(POST))   return insertGenre(req, res);

        return returnMessage(req, res, SC_BAD_REQUEST, "text-danger",
                "Metodo HTTP non supportato.");
    }

    private static String dispatchAlbum(Request req, Response res) {
        String userMethod = req.queryParamOrDefault("method", "GET");
        if(userMethod.equalsIgnoreCase(GET))    return getAlbums(req, res);
        if(userMethod.equalsIgnoreCase(POST))   return insertAlbum(req, res);
        if(userMethod.equalsIgnoreCase(DELETE)) return deleteAlbum(req, res);

        return returnMessage(req, res, SC_BAD_REQUEST, "text-danger",
                "Metodo HTTP non supportato.");
    }

    private static String dispatchArtist(Request req, Response res) {
        String userMethod = req.queryParamOrDefault("method", "GET");
        if(userMethod.equalsIgnoreCase(GET))    return getArtists(req, res);
        if(userMethod.equalsIgnoreCase(POST))   return insertArtist(req, res);
        if(userMethod.equalsIgnoreCase(PUT))    return updateArtist(req, res);

        return returnMessage(req, res, SC_BAD_REQUEST, "text-danger",
                "Metodo HTTP non supportato.");
    }


    /**
     * Sezione per l'esecuzione di query
     */


    private static String getMusic(Request req, Response res) {
        int pageNum = 0;

        PostgreSQLImpl db = PostgreSQLImpl.getInstance();
        if (db == null) {
            return handleInternalError(req, res);
        }

        if(req.queryParams("page") != null) {
            if(!isNumber(req.queryParams("page"))) {
                return handleParseError(req, res);
            } else {
                pageNum = Integer.parseInt(req.queryParams("page"));
            }
        }

        List<Music> musicList = db.getAllMusic(pageNum);
        if (musicList == null) {
            return handleInternalError(req, res);
        }
        if (musicList.isEmpty()) {
            return handleNotFound(req, res);
        }

        res.status(SC_OK);

        info(musicList.toString());

        Map<String, Object> model = new HashMap<>();
        model.put("musicList", musicList);
        model.put("page", pageNum);
        return engine.render(new ModelAndView(model, "musicList"));
    }

    private static String getMusicById(Request req, Response res) {
        PostgreSQLImpl db = PostgreSQLImpl.getInstance();
        if (db == null) {
            return handleInternalError(req, res);
        }

        int musicId;
        try {
            musicId = Integer.parseInt(req.params("id"));
        } catch (NumberFormatException e) {
            logger.error("Errore durante il parsing dell'id "+req.params("id"));
            return handleParseError(req, res);
        }

        List<Music> musicList = db.getMusicById(musicId);
        if (musicList == null) {
            return handleInternalError(req, res);
        }
        if (musicList.isEmpty()) {
            return handleNotFound(req, res);
        }

        res.status(SC_OK);

        info(musicList.toString());

        Map<String, Object> model = new HashMap<>();
        model.put("musicList", musicList);
        model.put("page", -100);
        return engine.render(new ModelAndView(model, "musicList"));
    }

    private static String upMusicForm(Request req, Response res) {
        Map<String, String> model = new HashMap<>();
        return engine.render(new ModelAndView(model, "upMusic"));
    }

    private static String insMusicForm(Request req, Response res) {
        Map<String, String> model = new HashMap<>();
        return engine.render(new ModelAndView(model, "insMusic"));
    }

    private static String delMusicForm(Request req, Response res) {
        Map<String, String> model = new HashMap<>();
        return engine.render(new ModelAndView(model, "delMusic"));
    }

    private static String insertMusic(Request req, Response res) {
        PostgreSQLImpl db = PostgreSQLImpl.getInstance();
        if (db == null) {
            return handleInternalError(req, res);
        }

        Music musicToAdd = new Music();
        try {
            musicToAdd.setMusicId(Integer.parseInt(req.queryParams(MUSICID)));
            musicToAdd.setTitle(URLDecoder.decode(req.queryParams(TITLE), "UTF-8"));
            musicToAdd.setAuthorId(Integer.parseInt(req.queryParams(AUTHORID)));
            if (req.queryParams(ALBUMID) != null && !req.queryParams(ALBUMID).equals("")) {
                musicToAdd.setAlbumId(Integer.parseInt(req.queryParams(ALBUMID)));
            } else {
                musicToAdd.setAlbumId(-1000);
            }
            musicToAdd.setYear(Integer.parseInt(req.queryParams(YEAR)));
            musicToAdd.setGenreId(Integer.parseInt(req.queryParams(GENREID)));
        } catch (NumberFormatException | UnsupportedEncodingException e) {
            logger.warn("Errore nella deserializzazione della musica da inserire");
            return handleParseError(req, res);
        }

        int result = db.insertMusic(musicToAdd);
        if(result < 0) {
            if(result == -2) {
                return handleInternalError(req, res);
            }
            if(result == -1) {
                return returnMessage(req, res, SC_CONFLICT, "text-warning",
                        "Esiste gia' una musica con id "+musicToAdd.getMusicId()+".");
            }
        }

        return returnMessage(req, res, SC_CREATED, "text-success",
                "Musica con id "+musicToAdd.getMusicId()+" aggiunta con successo.");
    }

    private static String updateMusic(Request req, Response res) {
        PostgreSQLImpl db = PostgreSQLImpl.getInstance();
        if (db == null) {
            return handleInternalError(req, res);
        }

        Music musicToUpdate = new Music();
        try {
            musicToUpdate.setMusicId(Integer.parseInt(req.queryParams(MUSICID)));
            musicToUpdate.setTitle(URLDecoder.decode(req.queryParams(TITLE), "UTF-8"));
            musicToUpdate.setAuthorId(Integer.parseInt(req.queryParams(AUTHORID)));
            if (req.queryParams(ALBUMID) != null && !req.queryParams(ALBUMID).equals("")) {
                musicToUpdate.setAlbumId(Integer.parseInt(req.queryParams(ALBUMID)));
            } else {
                musicToUpdate.setAlbumId(-1000);
            }
            musicToUpdate.setYear(Integer.parseInt(req.queryParams(YEAR)));
            musicToUpdate.setGenreId(Integer.parseInt(req.queryParams(GENREID)));
        } catch (NumberFormatException | UnsupportedEncodingException e) {
            logger.warn("Errore nella deserializzazione della musica da aggiornare");
            return handleParseError(req, res);
        }

        int result = db.updateMusic(musicToUpdate);
        if(result < 0) {
            if(result == -2) {
                return handleInternalError(req, res);
            }
            if(result == -1) {
                return returnMessage(req, res, SC_BAD_REQUEST, "text-warning",
                        "Non esiste una musica con id "+musicToUpdate.getMusicId()+", " +
                                "impossibile aggiornarla.");
            }
        }

        return returnMessage(req, res, SC_OK, "text-success",
                "Musica con id "+musicToUpdate.getMusicId()+" modificata con successo.");
    }

    private static String deleteMusic(Request req, Response res) {
        PostgreSQLImpl db = PostgreSQLImpl.getInstance();
        if (db == null) {
            return handleInternalError(req, res);
        }

        if(req.queryParams(MUSICID) == null || !isNumber(req.queryParams(MUSICID))) {
            return returnMessage(req, res, SC_BAD_REQUEST, "text-danger",
                    "Specificare un id nel formato corretto.");
        }

        int musicId = Integer.parseInt(req.queryParams(MUSICID));
        int result = db.deleteMusic(musicId);
        if(result < 0) {
            if(result == -2) {
                return handleInternalError(req, res);
            }
            if(result == -1) {
                return returnMessage(req, res, SC_BAD_REQUEST, "text-warning",
                        "Non esiste una musica con id "+musicId+", " +
                        "impossibile eliminarla.");
            }
        }

        return returnMessage(req, res, SC_OK, "text-success",
                "Musica con id "+musicId+" eliminata con successo.");

    }

    private static String getAlbums(Request req, Response res) {
        PostgreSQLImpl db = PostgreSQLImpl.getInstance();
        if (db == null) {
            return handleInternalError(req, res);
        }

        List<Album> albumList = db.getAllAlbums();
        if (albumList == null) {
            return handleInternalError(req, res);
        }
        if (albumList.isEmpty()) {
            return handleNotFound(req, res);
        }

        res.status(SC_OK);

        info(albumList.toString());

        Map<String, Object> model = new HashMap<>();
        model.put("albumList", albumList);
        return engine.render(new ModelAndView(model, "albumList"));
    }

    private static String insAlbumForm(Request req, Response res) {
        Map<String, String> model = new HashMap<>();
        return engine.render(new ModelAndView(model, "insAlbum"));
    }

    private static String delAlbumForm(Request req, Response res) {
        Map<String, String> model = new HashMap<>();
        return engine.render(new ModelAndView(model, "delAlbum"));
    }

    private static String insertAlbum(Request req, Response res) {
        PostgreSQLImpl db = PostgreSQLImpl.getInstance();
        if (db == null) {
            return handleInternalError(req, res);
        }

        Album albumToAdd = new Album();
        try {
            albumToAdd.setAlbumId(Integer.parseInt(req.queryParams(ALBUMID)));
            albumToAdd.setTitle(URLDecoder.decode(req.queryParams(TITLE), "UTF-8"));
            albumToAdd.setYear(Integer.parseInt(req.queryParams(YEAR)));
            albumToAdd.setGroupId(Integer.parseInt(req.queryParams(GROUPID)));
        } catch (NumberFormatException | UnsupportedEncodingException e) {
            logger.warn("Errore nella deserializzazione dell' album da inserire");
            return handleParseError(req, res);
        }

        int result = db.insertAlbum(albumToAdd);
        if(result < 0) {
            if(result == -2) {
                return handleInternalError(req, res);
            }
            if(result == -1) {
                return returnMessage(req, res, SC_CONFLICT, "text-warning",
                        "Esiste gia' un album con id "+albumToAdd.getAlbumId()+".");
            }
        }

        return returnMessage(req, res, SC_CREATED, "text-success",
                "Album con id "+albumToAdd.getAlbumId()+" aggiunto con successo.");
    }

    private static String deleteAlbum(Request req, Response res) {
        PostgreSQLImpl db = PostgreSQLImpl.getInstance();
        if (db == null) {
            return handleInternalError(req, res);
        }

        if(req.queryParams(ALBUMID) == null || !isNumber(req.queryParams(ALBUMID))) {
            return returnMessage(req, res, SC_BAD_REQUEST, "text-danger",
                    "Specificare un id nel formato corretto.");
        }

        int albumId = Integer.parseInt(req.queryParams(ALBUMID));
        int result = db.deleteAlbum(albumId);
        if(result < 0) {
            if(result == -2) {
                return handleInternalError(req, res);
            }
            if(result == -1) {
                return returnMessage(req, res, SC_BAD_REQUEST, "text-warning",
                        "Non esiste un album con id "+ albumId +", " +
                                "impossibile eliminarlo.");
            }
        }

        return returnMessage(req, res, SC_OK, "text-success",
                "Album con id "+ albumId +" eliminato con successo.");

    }

    private static String getArtists(Request req, Response res) {
        PostgreSQLImpl db = PostgreSQLImpl.getInstance();
        if (db == null) {
            return handleInternalError(req, res);
        }

        List<Artist> artistList = db.getAllArtists();
        if (artistList == null) {
            return handleInternalError(req, res);
        }
        if (artistList.isEmpty()) {
            return handleNotFound(req, res);
        }

        res.status(SC_OK);

        info(artistList.toString());

        Map<String, Object> model = new HashMap<>();
        model.put("artistList", artistList);
        return engine.render(new ModelAndView(model, "artistList"));
    }

    private static String upArtistForm(Request req, Response res) {
        Map<String, String> model = new HashMap<>();
        return engine.render(new ModelAndView(model, "upArtist"));
    }

    private static String insArtistForm(Request req, Response res) {
        Map<String, String> model = new HashMap<>();
        return engine.render(new ModelAndView(model, "insArtist"));
    }

    private static String updateArtist(Request req, Response res) {
        PostgreSQLImpl db = PostgreSQLImpl.getInstance();
        if (db == null) {
            return handleInternalError(req, res);
        }

        Artist artistToUpdate = new Artist();
        try {
            artistToUpdate.setArtistId(Integer.parseInt(req.queryParams(ARTISTID)));
            artistToUpdate.setName(URLDecoder.decode(req.queryParams(NAME), "UTF-8"));
            artistToUpdate.setGroupId(Integer.parseInt(req.queryParams(GROUPID)));
        } catch (NumberFormatException | UnsupportedEncodingException e) {
            logger.warn("Errore nella deserializzazione dell' artista da aggiornare");
            return handleParseError(req, res);
        }

        int result = db.updateArtist(artistToUpdate);
        if(result < 0) {
            if(result == -2) {
                return handleInternalError(req, res);
            }
            if(result == -1) {
                return returnMessage(req, res, SC_BAD_REQUEST, "text-warning",
                        "Non esiste un artista con id "+ artistToUpdate.getArtistId()+", " +
                                "impossibile aggiornarlo.");
            }
        }

        return returnMessage(req, res, SC_OK, "text-success",
                "Artista con id "+ artistToUpdate.getArtistId()+" modificato con successo.");
    }

    private static String insertArtist(Request req, Response res) {
        PostgreSQLImpl db = PostgreSQLImpl.getInstance();
        if (db == null) {
            return handleInternalError(req, res);
        }

        Artist artistToAdd = new Artist();
        try {
            artistToAdd.setArtistId(Integer.parseInt(req.queryParams(ARTISTID)));
            artistToAdd.setName(URLDecoder.decode(req.queryParams(NAME), "UTF-8"));
            artistToAdd.setGroupId(Integer.parseInt(req.queryParams(GROUPID)));
        } catch (NumberFormatException | UnsupportedEncodingException e) {
            logger.warn("Errore nella deserializzazione dell' artista da inserire");
            return handleParseError(req, res);
        }

        int result = db.insertArtist(artistToAdd);
        if(result < 0) {
            if(result == -2) {
                return handleInternalError(req, res);
            }
            if(result == -1) {
                return returnMessage(req, res, SC_CONFLICT, "text-warning",
                        "Esiste gia' un artista con id "+ artistToAdd.getArtistId()+".");
            }
        }

        return returnMessage(req, res, SC_CREATED, "text-success",
                "Artista con id "+ artistToAdd.getArtistId()+" aggiunto con successo.");
    }

    private static String getGroups(Request req, Response res) {
        PostgreSQLImpl db = PostgreSQLImpl.getInstance();
        if (db == null) {
            return handleInternalError(req, res);
        }

        List<Group> groupList = db.getAllGroups();
        if (groupList == null) {
            return handleInternalError(req, res);
        }
        if (groupList.isEmpty()) {
            return handleNotFound(req, res);
        }

        res.status(SC_OK);

        info(groupList.toString());

        Map<String, Object> model = new HashMap<>();
        model.put("groupList", groupList);
        return engine.render(new ModelAndView(model, "groupList"));
    }

    private static String insGroupForm(Request req, Response res) {
        Map<String, String> model = new HashMap<>();
        return engine.render(new ModelAndView(model, "insGroup"));
    }

    private static String insertGroup(Request req, Response res) {
        PostgreSQLImpl db = PostgreSQLImpl.getInstance();
        if (db == null) {
            return handleInternalError(req, res);
        }

        Group groupToAdd = new Group();
        try {
            groupToAdd.setGroupId(Integer.parseInt(req.queryParams(GROUPID)));
            groupToAdd.setName(URLDecoder.decode(req.queryParams(NAME), "UTF-8"));
        } catch (NumberFormatException | UnsupportedEncodingException e) {
            logger.warn("Errore nella deserializzazione del gruppo da inserire");
            return handleParseError(req, res);
        }

        int result = db.insertGroup(groupToAdd);
        if(result < 0) {
            if(result == -2) {
                return handleInternalError(req, res);
            }
            if(result == -1) {
                return returnMessage(req, res, SC_CONFLICT, "text-warning",
                        "Esiste gia' un gruppo con id "+groupToAdd.getGroupId()+".");
            }
        }

        return returnMessage(req, res, SC_CREATED, "text-success",
                "Gruppo con id "+groupToAdd.getGroupId()+" aggiunto con successo.");
    }

    private static String getGenres(Request req, Response res) {
        PostgreSQLImpl db = PostgreSQLImpl.getInstance();
        if (db == null) {
            return handleInternalError(req, res);
        }

        List<Genre> genreList = db.getAllGenres();
        if (genreList == null) {
            return handleInternalError(req, res);
        }
        if (genreList.isEmpty()) {
            return handleNotFound(req, res);
        }

        res.status(SC_OK);

        info(genreList.toString());

        Map<String, Object> model = new HashMap<>();
        model.put("genreList", genreList);
        return engine.render(new ModelAndView(model, "genreList"));
    }

    private static String insGenreForm(Request req, Response res) {
        Map<String, String> model = new HashMap<>();
        return engine.render(new ModelAndView(model, "insGenre"));
    }

    private static String insertGenre(Request req, Response res) {
        PostgreSQLImpl db = PostgreSQLImpl.getInstance();
        if (db == null) {
            return handleInternalError(req, res);
        }

        Genre genreToAdd = new Genre();
        try {
            genreToAdd.setGenreId(Integer.parseInt(req.queryParams(GENREID)));
            genreToAdd.setName(URLDecoder.decode(req.queryParams(NAME), "UTF-8"));
        } catch (NumberFormatException | UnsupportedEncodingException e) {
            logger.warn("Errore nella deserializzazione del genere da inserire");
            return handleParseError(req, res);
        }

        int result = db.insertGenre(genreToAdd);
        if(result < 0) {
            if(result == -2) {
                return handleInternalError(req, res);
            }
            if(result == -1) {
                return returnMessage(req, res, SC_CONFLICT, "text-warning",
                        "Esiste gia' un genere con id "+ genreToAdd.getGenreId()+".");
            }
        }

        return returnMessage(req, res, SC_CREATED, "text-success",
                "Genere con id "+ genreToAdd.getGenreId()+" aggiunto con successo.");
    }

    private static String getLinks(Request req, Response res) {
        PostgreSQLImpl db = PostgreSQLImpl.getInstance();
        if (db == null) {
            return handleInternalError(req, res);
        }

        List<Link> linkList = db.getAllLinks();
        if (linkList == null) {
            return handleInternalError(req, res);
        }
        if (linkList.isEmpty()) {
            return handleNotFound(req, res);
        }

        res.status(SC_OK);

        info(linkList.toString());

        Map<String, Object> model = new HashMap<>();
        model.put("linkList", linkList);
        return engine.render(new ModelAndView(model, "linkList"));
    }

    private static String musicJoinLink(Request req, Response res) {
        PostgreSQLImpl db = PostgreSQLImpl.getInstance();
        if(db == null) {
            return handleInternalError(req, res);
        }

        List<MusicJoinLink> musicJoinLinkList = db.musicJoinLink();
        if (musicJoinLinkList == null) {
            return handleInternalError(req, res);
        }
        if (musicJoinLinkList.isEmpty()) {
            return handleNotFound(req, res);
        }

        res.status(SC_OK);

        info(musicJoinLinkList.toString());

        Map<String, Object> model = new HashMap<>();
        model.put("musicJoinLinkList", musicJoinLinkList);
        return engine.render(new ModelAndView(model, "musicJoinLink"));
    }

    private static String artistJoinGroup(Request req, Response res) {
        PostgreSQLImpl db = PostgreSQLImpl.getInstance();
        if(db == null) {
            return handleInternalError(req, res);
        }

        List<ArtistJoinGroup> artistJoinGroupList = db.artistJoinGroup();
        if (artistJoinGroupList == null) {
            return handleInternalError(req, res);
        }
        if (artistJoinGroupList.isEmpty()) {
            return handleNotFound(req, res);
        }

        res.status(SC_OK);

        info(artistJoinGroupList.toString());

        Map<String, Object> model = new HashMap<>();
        model.put("artistJoinGroupList", artistJoinGroupList);
        return engine.render(new ModelAndView(model, "artistJoinGroup"));
    }

    private static String joinAll(Request req, Response res) {
        PostgreSQLImpl db = PostgreSQLImpl.getInstance();
        if(db == null) {
            return handleInternalError(req, res);
        }

        List<JoinAll> joinAllList = db.joinAll();
        if (joinAllList == null) {
            return handleInternalError(req, res);
        }
        if (joinAllList.isEmpty()) {
            return handleNotFound(req, res);
        }

        res.status(SC_OK);

        info(joinAllList.toString());

        Map<String, Object> model = new HashMap<>();
        model.put("joinAllList", joinAllList);
        return engine.render(new ModelAndView(model, "joinAll"));
    }

    /**
     * Funzione per fornire l'iconcina  di fianco al titolo.
     * Adattato dalla seguente fonte:
     * @author hamishmorgan
     * https://github.com/hamishmorgan/ERL/blob/master/src/test/java/spark/SparkExamples.java
     */
    private static String favicon(Request req, Response res) {
        try {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new BufferedInputStream(new FileInputStream(".\\favicon.ico"));
                out = new BufferedOutputStream(res.raw().getOutputStream());
                res.raw().setContentType("image/x-icon");
                res.status(SC_OK);
                ByteStreams.copy(in, out);
                out.flush();
                return "";
            } finally {
                Closeables.close(in, true);
            }
        } catch (FileNotFoundException ex) {
            logger.warn(ex.getMessage());
            res.status(SC_NOT_FOUND);
            return ex.getMessage();
        } catch (IOException ex) {
            logger.warn(ex.getMessage());
            res.status(SC_INTERNAL_SERVER_ERROR);
            return ex.getMessage();
        }
    }

}
