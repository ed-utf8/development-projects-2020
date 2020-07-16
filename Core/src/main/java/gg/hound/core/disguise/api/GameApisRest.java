package gg.hound.core.disguise.api;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GameApisRest {

    private final Gson gson = new Gson();

    public Profile getProfile(String uuidOrName) {
        String ip = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuidOrName.replaceAll("-", "") + "?unsigned=false";
        String json = readJson(ip);
        return gson.fromJson(json, Profile.class);
    }

    private String readJson(String ip) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(ip);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.addRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            http.setConnectTimeout(3000);
            http.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream(), StandardCharsets.UTF_8));
            String l;
            while ((l = reader.readLine()) != null) {
                sb.append(l);
            }
            reader.close();

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return sb.toString();
    }
}
