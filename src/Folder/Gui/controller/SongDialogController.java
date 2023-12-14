package Folder.Gui.controller;

import Folder.Be.Song;
import Folder.Gui.model.SongDialogModel;
import Folder.Gui.util.TimeStringConverter;
import Folder.Gui.view.SongDialogViewBuilder;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;

/**
 * Manages the dialog for creating or editing a song.
 * It controls the interaction between the user interface and an underlying data model.
 */
public class SongDialogController implements IDialogController<Song> {
    private final SongDialogModel model;
    private final SongDialogViewBuilder dialogViewBuilder;
    private Dialog<Song> dialog;

    /**
     * Initializes a new SongDialogController with a specific song and a list of genres.
     *
     * @param song the Song object to be edited or null if creating a new song.
     * @param genres the observable list of music genres available for selection.
     */
    public SongDialogController(Song song, ObservableList<String> genres) {
        this.model = new SongDialogModel();

        model.setProperties(song);

        this.dialogViewBuilder = new SongDialogViewBuilder(model, genres, this::chooseFile);
    }

    @Override
    public Region getView() {
        return dialogViewBuilder.build();
    }

    @Override
    public void initializeDialog(Dialog<Song> dialog) {
        this.dialog = dialog;
        setupDialog();
    }

    private void setupDialog() {
        Node okBtn = dialog.getDialogPane().lookupButton(ButtonType.OK);
        model.isValidInputProperty().bind(Bindings.createBooleanBinding(this::isDataValid, model.getBindableProperties()));
        okBtn.disableProperty().bind(model.isValidInputProperty().not());

        dialog.setResultConverter(dialogBtn -> {
            if (dialogBtn == ButtonType.OK) {
                String title = model.getTitle();
                String artist = model.getArtist();
                String genre = model.getGenre();
                int duration = new TimeStringConverter().fromString(model.getDuration());
                String filePath = model.getFilePath();

                if (model.getId() == -1) {
                    return new Song(-1, title, artist, genre, duration, filePath);
                } else {
                    return new Song(model.getId(), title, artist, genre, duration, filePath);
                }
            }
            return null;
        });
    }

    private void chooseFile(TextField fieldFilePath) {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Music Files", "*.mp3", "*.wav");
        chooser.getExtensionFilters().add(filter);

        File selectedFile = chooser.showOpenDialog(null);
        if (selectedFile != null) {
            fieldFilePath.setText(selectedFile.getAbsolutePath());
            getMetadataFromFile(selectedFile);
        }
    }

    private void getMetadataFromFile(File file) {
        Media media = new Media(file.toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setOnReady(() -> {
            String artist = (String) media.getMetadata().get("artist");
            String title = (String) media.getMetadata().get("title");
            Duration duration = media.getDuration();
            int millis = (int) duration.toMillis();

            model.setArtist(artist != null ? artist : "");
            model.setTitle(title != null ? title : "");
            model.setDuration(millis);
        });
        //mediaPlayer.dispose();
    }

    private boolean isDataValid() {
        return !model.getTitle().trim().isEmpty() &&
                !model.getArtist().trim().isEmpty() &&
                !model.getGenre().trim().isEmpty() &&
                !model.getDuration().trim().isEmpty() &&
                !model.getFilePath().trim().isEmpty();
    }
}