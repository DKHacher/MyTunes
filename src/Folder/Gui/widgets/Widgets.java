package Folder.Gui.widgets;

import Folder.Bll.AutocompleteLogic;
import Folder.Common.IAutocompleteHandler;
import Folder.Gui.util.TimeFilter;
import Folder.Gui.util.TimeStringConverter;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.function.Consumer;

public class Widgets {
    public static Node promptLabel(String contents) {
        return new Label(contents);
    }


    public static Node promptedTextField(String prompt, StringProperty boundProperty) {
        return new HBox(6, promptLabel(prompt), boundTextField(boundProperty));
    }

    public static TextField boundTextField(StringProperty boundProperty) {
        return boundTextField(boundProperty, true);
    }

    public static TextField boundTextField(StringProperty boundProperty, boolean isEditable) {
        TextField textField = new TextField();
        textField.textProperty().bindBidirectional(boundProperty);
        textField.setEditable(isEditable);
        return textField;
    }

    public static Node promptedAutocompletTextField(String prompt, List<String> items, StringProperty boundProperty) {
        return new HBox(6, promptLabel(prompt), boundAutocompleteTextField(items, boundProperty));
    }

    public static Node boundAutocompleteTextField(List<String> items, StringProperty boundProperty) {
        IAutocompleteHandler handler = new AutocompleteLogic();

        AutocompleteTextField autocompleteField = new AutocompleteTextField(handler);
        autocompleteField.textProperty().bindBidirectional(boundProperty);
        handler.populateEntires(items);

        return autocompleteField;
    }

    public static Node promptedFormattedTextField(String prompt, StringProperty boundProperty, TimeFilter filter, TimeStringConverter converter) {
        return new HBox(6, promptLabel(prompt), boundFormattedTextField(boundProperty, filter, converter));
    }

    public static Node boundFormattedTextField(StringProperty boundProperty, TimeFilter filter, TimeStringConverter converter) {
        TextField textField = new TextField();
        textField.setTextFormatter(new TextFormatter<>(converter, 0, filter));
        textField.textProperty().bindBidirectional(boundProperty);
        return textField;
    }


    public static Node chooseActionButton(String btnText, TextField textField, Consumer<TextField> fileChooseAction) {
        Button btn = new Button(btnText);
        btn.setOnAction(evt -> fileChooseAction.accept(textField));

        return btn;
    }
}
