package Folder.Gui.util;

import javafx.util.StringConverter;

public class TimeStringConverter extends StringConverter<Integer> {
    @Override
    public String toString(Integer object) {
        if (object == null) {
            return "";
        }

        int hours = object / 3600000;
        int minutes = (object % 3600000) / 60000;
        int seconds = (object % 60000) / 1000;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public Integer fromString(String string) {
        if (string == null || string.trim().isEmpty()) {
            return 0;
        }

        String[] parts = string.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
        return (hours * 3600 + minutes * 60 + seconds) * 1000;
    }
}