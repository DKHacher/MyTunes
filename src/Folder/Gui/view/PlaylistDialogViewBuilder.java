package Folder.Gui.view;

import Folder.Gui.model.

        PlaylistDialogModel;
import Folder.Gui.util.TimeFilter;
import Folder.Gui.util.TimeStringConverter;
import Folder.Gui.widgets.Widgets;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.util.Builder;

import java.util.List;
import java.util.function.Consumer;

public class PlaylistDialogViewBuilder implements Builder<Region> {
    public enum Mode {
        CREATE,
        EDIT
    }



    PlaylistDialogModel model;
    List<String> genres;
    private Consumer<TextField> fileChooserConsumer;

    public PlaylistDialogViewBuilder(

            PlaylistDialogModel model) {
        this.model = model;
    }

    @Override
    public Region build() {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        int row = 0;
        gridPane.addRow(row++, Widgets.promptLabel("Name:"), Widgets.boundTextField(model.nameProperty()));

        return gridPane;
    }
}
