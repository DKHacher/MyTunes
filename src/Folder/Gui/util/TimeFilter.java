package Folder.Gui.util;

import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class TimeFilter implements UnaryOperator<TextFormatter.Change> {
    private static final Pattern TIME_PATTERN = Pattern.compile("\\d{1,2}:\\d{0,2}");

    @Override
    public TextFormatter.Change apply(TextFormatter.Change change) {
        String newText = change.getControlNewText();
        if (TIME_PATTERN.matcher(newText).matches()) {
            return change;
        }
        return null;
    }
}