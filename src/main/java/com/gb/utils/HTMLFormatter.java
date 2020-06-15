package com.gb.utils;

import com.gb.modelObject.*;
import static com.gb.Constants.*;
import java.util.List;

public class HTMLFormatter {

    public static final String HTML_INIT =
            "<html><head><link rel=\"stylesheet\" type=\"text/css\"" +
            "href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css\"></link>" +
            "<style>body { background-image: url('"+BG_IMG_NAME+"'); background-size: cover;}</style>" +
            "</head>" +
            "<body><div class=\"container\" style=\"margin-top:25px\">";

    public static final String HTML_END =
            "</div></tbody></table></body></html>";

    private static void Itemize(StringBuilder sb, String content) {
        sb.append("<td>");
        sb.append(content);
        sb.append("</td>");
    }

    /**
     * Creates an HTML table to show a music list, e.g. through a Web Browser.<br>
     * E.g. The list of all music in the database.
     * @param musicList The sensor list to be shown.
     * @return An HTML table containing the music list.
     */
    public static String FormatMusic(List<Music> musicList) {

        String html =
                 HTML_INIT +
                        "<h1 class=\"display-1 text-primary\">Tabella Music</h1>" +
                        "<table align=\"center\" class=\"table table-striped table-dark\" width=\"50%\" cellpadding=\"5\">" +
                        "<thead class=\"thead-dark\"><tr><th>ID</th><th>Title</th><th>AuthorID</th><th>AlbumID</th><th>Year</th>" +
                        "<th>GenreID</th></tr></thead><tbody>";

        StringBuilder sb = new StringBuilder();

        sb.append(html);

        for(Music item : musicList) {
            sb.append("<tr>");
            Itemize(sb, ""+item.getMusicId());
            Itemize(sb, item.getTitle());
            Itemize(sb, ""+item.getAuthorId());
            Itemize(sb, ""+item.getAlbumId());
            Itemize(sb, ""+item.getYear());
            Itemize(sb, ""+item.getGenreId());
            sb.append("</tr>");
        }

        sb.append(HTML_END);

        return sb.toString();

    }

    /**
     * Creates an HTML table to show a one-column list, e.g. through a Web Browser.<br>
     * E.g. The list of all sensor protocols in the database.
     * @param stringList The List to be shown.
     * @param itemName The name or category of the items shown (E.g. "Protocols").
     * @return An HTML table containing the list, with a leading row specifying the contents.
     */
    public static String FormatOneColumnList(List<String> stringList, String itemName) {

        String html =
                "<html>" +
                        "<head></head>" +
                        "<body>" +
                        "<table align=\"center\" border=\"1\" cellpadding=\"5\">" +
                        "<thead><tr><th>"+itemName+"</th></tr></thead><tbody>";

        StringBuilder sb = new StringBuilder();

        sb.append(html);

        for(String item : stringList) {
            sb.append("<tr>");
            Itemize(sb, item);
            sb.append("</tr>");
        }

        sb.append("</tbody></table></body></html>");

        return sb.toString();

    }

    /**
     * Formats an HTML message to be shown (in the most simple way), e.g. in a Web Browser.
     * E.g. "Sensor inserted successfully".
     * @param message The message to be shown.
     * @return An HTML page containing the message.
     */
    public static String FormatMessage(String message) {

        return "<html><head></head><body>" + message + "</body></html>";

    }

}
