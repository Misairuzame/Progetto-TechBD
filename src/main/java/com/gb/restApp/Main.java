package com.gb.restApp;

import static spark.Spark.*;
import com.gb.db.PostgreSQLImpl.PostgreSQLImpl;
import com.gb.modelObject.Music;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            get("",  Main::getMusic);

            post("",  Main::addOne);
            post("/", Main::addOne);

            delete("",  Main::deleteAll);
            delete("/", Main::deleteAll);

            put("/:id", Main::updateOne);

            delete("/:id", Main::deleteOne);
        });

        get("/favicon.ico", Main::favicon);

        notFound(Main::handleNotFound);

        options("/*", Main::allowCORS);

    }

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

    private static String handleUnsupportedMediaType(Request req, Response res) {
        return returnMessage(req, res, SC_UNSUPPORTED_MEDIA_TYPE, "text-danger",
                "Il media type specificato non e' supportato.");
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

    private static String getMusic(Request req, Response res) {
        int pageNum = 0;

        PostgreSQLImpl db = PostgreSQLImpl.getInstance();
        if (db == null) {
            return handleInternalError(req, res);
        }

        if(req.queryParams("page") != null) {
            if(!isNumber(req.queryParams("page"))) {
                return returnMessage(req, res, SC_BAD_REQUEST, "text-danger",
                        "Specificare la pagina in maniera corretta.");
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
        return engine.render(new ModelAndView(model, "musicList"));
    }

    private static String addOne(Request req, Response res) {
        if(req.contentType() == null || !req.contentType().equals(TEXT_HTML)) {
            return handleUnsupportedMediaType(req, res);
        }

        PostgreSQLImpl db = PostgreSQLImpl.getInstance();
        if (db == null) {
            return handleInternalError(req, res);
        }

        Music musicToAdd = new Music();
        try {
            //TODO: aggiungere deserializzazione parametri HTML
        } catch (NumberFormatException e) {
            //TODO: restituire messaggio danger
        }
        int result = db.insertMusic(musicToAdd);
        if(result < 0) {
            if(result == -2) {
                return handleInternalError(req, res);
            }
            if(result == -1) {
                return returnMessage(req, res, SC_CONFLICT, "text-warning",
                        "Esiste già una musica con id "+musicToAdd.getMusicId()+".");
            }
        }

        return returnMessage(req, res, SC_CREATED, "text-success",
                "Musica con id "+musicToAdd.getMusicId()+" aggiunta con successo.");
    }

    private static String updateOne(Request req, Response res) {
        if(req.contentType() == null || !req.contentType().equals(TEXT_HTML)) {
            return handleUnsupportedMediaType(req, res);
        }

        PostgreSQLImpl db = PostgreSQLImpl.getInstance();
        if (db == null) {
            return handleInternalError(req, res);
        }

        Music musicToUpdate;
        Music musicToAdd = new Music();
        try {
            //TODO: aggiungere deserializzazione parametri HTML
        } catch (NumberFormatException e) {
            //TODO: restituire messaggio danger
        } catch(JsonSyntaxException e) {
            logger.error("Errore nella deserializzazione del JSON: "+e.getMessage());
            return handleParseError(req, res);
        }
        if(musicToUpdate == null) {
            return handleInternalError(req, res);
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

    private static String deleteOne(Request req, Response res) {
        PostgreSQLImpl db = PostgreSQLImpl.getInstance();
        if (db == null) {
            return handleInternalError(req, res);
        }

        if(req.params("id") == null || !isNumber(req.params("id"))) {
            return returnMessage(req, res, SC_BAD_REQUEST, "text-danger",
                    "Specificare un id nel formato corretto.");
        }

        int musicId = Integer.parseInt(req.params("id"));
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

    private static String deleteAll(Request req, Response res) {
        return returnMessage(req, res, SC_FORBIDDEN, "text-danger",
                "L'eliminazione dell'intera collezione e' vietata.");
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
