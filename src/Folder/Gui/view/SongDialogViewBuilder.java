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

/**
 * Responsible for building the user interface for the song dialog.<br>
 * <br>
 * It creates a layout to display and edit properties of a song, including title, artist, genre, duration, and file selection.<br>
 * The class uses a SongDialogModel to bind UI elements to the data model and handles user interactions for file selection.
 */
public class SongDialogViewBuilder implements Builder<Region> {
    private final SongDialogModel model;
    private final List<String> genres;
    private final Consumer<TextField> fileChooserConsumer;

    /**
     * Initializes a new SongDialogViewBuilder with the specified model, genre list, and file chooser functionality.
     *
     * @param model the SongDialogModel that holds the data for the song
     * @param genres a list of strings representing the available music genres
     * @param fileChooserConsumer a consumer that handles the action of choosing a file
     */
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

        TextField fileChooserField = Widgets.boundTextField(model.filePathProperty(), false);

        int row = 0;
        gridPane.addRow(row++, Widgets.promptLabel("Title:"), Widgets.boundTextField(model.titleProperty()));
        gridPane.addRow(row++, Widgets.promptLabel("Artist:"), Widgets.boundTextField(model.artistProperty()));
        gridPane.addRow(row++, Widgets.promptLabel("Genre:"), Widgets.boundAutocompleteTextField(genres, model.genreProperty()));
        gridPane.addRow(row++, Widgets.promptLabel("Duration:"), Widgets.boundFormattedTextField(model.durationProperty(), new TimeFilter(), new TimeStringConverter()));

        if (model.getFilePath().isEmpty()) {
            gridPane.addRow(row++, Widgets.promptLabel("File:"), fileChooserField, Widgets.chooseActionButton("Choose...", fileChooserField, fileChooserConsumer));
        } else {
            gridPane.addRow(row++, Widgets.promptLabel("File:"), fileChooserField);
        }

        return gridPane;
    }
}
