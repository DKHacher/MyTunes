package Folder.Gui.widgets;

import Folder.Common.IAutocompleteHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.*;

public class AutocompleteTextField extends TextField {
    private final IAutocompleteHandler handler;
    private final ContextMenu suggestions;
    private int currSuggestionId = -1;

    public AutocompleteTextField(IAutocompleteHandler handler) {
        super();
        this.handler = handler;
        this.suggestions = new ContextMenu();

        addTextChangeListener();
        addKeyEventHandler();
        addFocusListener();
    }
    private void addTextChangeListener() {
        this.textProperty().addListener((obs, ov, nv) -> {
            updateSuggestions();
        });
    }

    private void addKeyEventHandler() {
        // Weird issue where one cannot navigate ContextMenu until it has been hovered or focused once.
        // This makes it, so we can navigate it using keyboard from the get go.
        this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (!suggestions.isShowing()) return;

            KeyCode code = event.getCode();
            if (code == KeyCode.DOWN || code == KeyCode.UP) {
                currSuggestionId = ensureWithinBounds(code);
                highlightMenuItem(currSuggestionId);
                event.consume();
            } else if (code == KeyCode.ENTER && isWithinBounds()) {
                selectCurrentSuggestion();
                event.consume();
            }
        });
    }

    private void addFocusListener() {
        this.focusedProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                updateSuggestions();
            } else {
                suggestions.hide();
            }
        });
    }

    private int ensureWithinBounds(KeyCode code) {
        if (code == KeyCode.DOWN) {
            return Math.min(currSuggestionId + 1, suggestions.getItems().size() - 1);
        } else {
            return Math.max(currSuggestionId - 1, 0);
        }
    }

    private boolean isWithinBounds() {
        return currSuggestionId >= 0 && currSuggestionId < suggestions.getItems().size();
    }

    private void selectCurrentSuggestion() {
        CustomMenuItem item = (CustomMenuItem) suggestions.getItems().get(currSuggestionId);
        item.fire(); // Trigger action associated with item
        suggestions.hide();
    }

    private void highlightMenuItem(int index) {
        for (int i = 0; i < suggestions.getItems().size(); i++) {
            CustomMenuItem item = (CustomMenuItem) suggestions.getItems().get(i);
            item.getContent().setStyle(i == index ? "-fx-background-color: #5474de;" : "-fx-background-color: transparent;");
        }
    }

    private void updateSuggestions() {
        String enteredText = getText();
        if (enteredText == null || enteredText.isEmpty()) {
            suggestions.hide();
        } else {
            List<String> filteredEntries = handler.getFilteredEntries(enteredText);
            if (!filteredEntries.isEmpty()) {
                populateSuggestions(filteredEntries, enteredText);
                if (!suggestions.isShowing() && getScene() != null) {
                    suggestions.show(this, Side.BOTTOM, 0, 0);
                }
            }
        }
    }

    private void populateSuggestions(List<String> searchResult, String searchRequest) {
        List<CustomMenuItem> items = searchResult.stream()
                .limit(10)
                .map(result -> createMenuItem(result, searchRequest))
                .toList();

        suggestions.getItems().clear();
        suggestions.getItems().addAll(items);
    }

    private CustomMenuItem createMenuItem(String result, String searchRequest) {
        Label label = new Label();
        label.setGraphic(createHighlightTextFlow(result, searchRequest));
        label.setPrefHeight(10);
        CustomMenuItem item = new CustomMenuItem(label, true);

        item.setOnAction(actionEvent -> {
            setText(result);
            positionCaret(result.length());
            suggestions.hide();
        });

        return item;
    }

    private static TextFlow createHighlightTextFlow(String text, String textToHighlight) {
        int highlightStartIndex = text.toLowerCase().indexOf(textToHighlight.toLowerCase());

        Text before = new Text(text.substring(0, highlightStartIndex));

        Text highlight = new Text(text.substring(highlightStartIndex, highlightStartIndex + textToHighlight.length())); //instead of "filter" to keep all "case sensitive"
        highlight.setFill(Color.FIREBRICK);
        highlight.setFont(Font.font(null, FontWeight.BOLD, 12));

        Text after = new Text(text.substring(highlightStartIndex + textToHighlight.length()));

        return new TextFlow(before, highlight, after);
    }
}
