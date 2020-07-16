package gg.hound.core.util;

import org.apache.commons.lang.math.NumberUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

public class PluginUtils {

    private final Map<Integer, Colour> colourMap = new HashMap<>();
    private final List<UUID> colour = new ArrayList<>();

    public Long currentTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Universal"));
        return calendar.getTimeInMillis();
    }

    public String getDate(long milliseconds) {
        TimeZone timeZone = TimeZone.getTimeZone("Universal");
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(milliseconds);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(timeZone);

        long years = currentTime() + 315569520000L;
        if (calendar.getTimeInMillis() >= years) return "Never";
        return simpleDateFormat.format(calendar.getTime()) + " UTC";
    }

    public int getTime(String time) {
        if (time.equalsIgnoreCase("p") || time.equalsIgnoreCase("perm") || time.equalsIgnoreCase("permanent")) {
            int length = 10;
            length = length * 60 * 60 * 24 * 365;
            return length;
        }

        if (time.substring(0, 1).matches("[0-9]")) {
            try {
                char timeLength = time.charAt(time.length() - 1);
                int punishmentTime = Integer.parseInt(time.substring(0, time.length() - 1));
                if (punishmentTime < 1)
                    return -10;

                if (timeLength == 's' || timeLength == 'S')
                    punishmentTime = punishmentTime * 1;
                else if (timeLength == 'm' || timeLength == 'M')
                    punishmentTime = punishmentTime * 60;
                else if (timeLength == 'h' || timeLength == 'H')
                    punishmentTime = punishmentTime * 60 * 60;
                else if (timeLength == 'd' || timeLength == 'D')
                    punishmentTime = punishmentTime * 60 * 60 * 24;
                else if (timeLength == 'w' || timeLength == 'W')
                    punishmentTime = punishmentTime * 60 * 60 * 24 * 7;
                else if (timeLength == 'n' || timeLength == 'N')
                    punishmentTime = punishmentTime * 60 * 60 * 24 * 30;
                else if (timeLength == 'y' || timeLength == 'Y')
                    punishmentTime = punishmentTime * 60 * 60 * 24 * 365;
                else
                    return -30;

                return punishmentTime;
            } catch (Exception exception) {
                return -30;
            }
        }
        return -10;
    }

    public Map<Integer, Colour> getColourMap() {
        return colourMap;
    }

    public Colour getColour(int id) {
        return colourMap.get(id);
    }

    public void addColour(UUID uuid) {
        colour.add(uuid);
    }

    public void removeColour(UUID uuid) {
        colour.remove(uuid);
    }

    public List<UUID> getColour() {
        return colour;
    }

    public int calculateInventorySize(int value) {
        if (value <= 0) return 9;
        int quotient = (int) Math.ceil(value / 9.0);
        return quotient > 5 ? 54 : quotient * 9;
    }

    public long getUnmuteTime(int time) {
        return currentTime() + (time * 1000);
    }
}
