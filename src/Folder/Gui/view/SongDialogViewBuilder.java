package Folder.Gui.view;

import Folder.Gui.model.SongDialogModel;
import Folder.Gui.util.TimeFilter;
import Folder.Gui.util.TimeStringConverter;
import Folder.Gui.widgets.Widgets;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;

import java.util.List;
import java.util.function.Consumer;

public class SongDialogViewBuilder implements Builder<Region> {
    public enum Mode {
        CREATE,
        EDIT
    }

    SongDialogModel model;
    List<String> genres;
    private Consumer<TextField> fileChooserConsumer;

    public SongDialogViewBuilder(SongDialogModel model, List<String> genres, Consumer<TextField> fileChooserConsumer) {
        this.model = model;
        this.genres = genres;
        this.fileChooserConsumer = fileChooserConsumer;
    }

    @Override
    public Region build() {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        TextField fileChooserField = Widgets.boundTextField(model.filePathProperty());

        int row = 0;
        gridPane.addRow(row++, Widgets.promptLabel("Title:"), Widgets.boundTextField(model.titleProperty()));
        gridPane.addRow(row++, Widgets.promptLabel("Artist:"), Widgets.boundTextField(model.artistProperty()));
        gridPane.addRow(row++, Widgets.promptLabel("Genre:"), Widgets.boundAutocompleteTextField(genres, model.genreProperty()));
        gridPane.addRow(row++, Widgets.promptLabel("Duration:"), Widgets.boundFormattedTextField(model.durationProperty(), new TimeFilter(), new TimeStringConverter()));
        gridPane.addRow(row++, Widgets.promptLabel("File:"), fileChooserField, Widgets.chooseActionButton("Choose...", fileChooserField, fileChooserConsumer));

        return gridPane;
    }
}