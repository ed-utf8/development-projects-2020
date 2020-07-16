package eu.kiieranngd.advance.Utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class WebUtils {

    private Config config;
    public WebUtils(Config config) {this.config = config;}

    public String getText(String url) throws IOException {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        connection.addRequestProperty("User-Agent", "B1nzy's personal pc");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();

        return response.toString();
    }

    public String shortenUrl(String url) {
        try {
            HttpsURLConnection con = (HttpsURLConnection)
                    new URL("https://www.googleapis.com/urlshortener/v1/url?key="
                            + config.getGoogleAPIKey())
                            .openConnection();
            con.setRequestMethod("POST");
            con.addRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            JsonObject jo = new JsonObject();

            jo.addProperty("longUrl", url);

            con.getOutputStream().write(
                    jo.toString().getBytes());

            return new JsonParser().parse(
                    new InputStreamReader(con.getInputStream()))
                    .getAsJsonObject().get("id").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
