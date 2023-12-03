package Folder.Gui.controller;

import Folder.Be.Song;
import Folder.Gui.model.SongDialogModel;
import Folder.Gui.model.SongModel;
import Folder.Gui.util.TimeStringConverter;
import Folder.Gui.view.SongDialogViewBuilder;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.util.Optional;

public class MainController {
    @FXML private TableView tblSongs;
    @FXML private TableColumn<Song, String> colTitle;
    @FXML private TableColumn<Song, String> colArtist;
    @FXML private TableColumn<Song, String> colGenre;
    @FXML private TableColumn<Song, String> colDuration;

    SongDialogModel songDialogModel;
    private SongModel songModel;


    public MainController() {
        songDialogModel = new SongDialogModel();

        try {
            songModel = new SongModel();
        } catch (Exception e) {
            displayError(e);
            e.printStackTrace();
        }
    }

    public void initialize() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colDuration.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(new TimeStringConverter().toString(cellData.getValue().getDuration())));

        tblSongs.setItems(songModel.getObservableSongs());

    }

    @FXML
    private void newSong(ActionEvent event) {
        try {
            songModel.updateGenres();
            songDialog(SongDialogViewBuilder.Mode.CREATE, null);
        } catch (Exception e) {
            displayError(e);
            e.printStackTrace();
        }
    }

    @FXML
    private void editSong(ActionEvent event) {
        Song selectedSong = (Song) tblSongs.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            try {
                songModel.updateGenres();
                songDialog(SongDialogViewBuilder.Mode.EDIT, selectedSong);
                songDialogModel.reset();
            } catch (Exception e) {
                displayError(e);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void deleteSong(ActionEvent event) {
        Song selectedSong = (Song) tblSongs.getSelectionModel().getSelectedItem();

        if (selectedSong != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Song");
            alert.setContentText("Are you sure you want to delete the song: " + selectedSong.getTitle() + " by " + selectedSong.getArtist() + "?");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    songModel.deleteSong(selectedSong);
                } catch (Exception e) {
                    displayError(e);
                }
            }
        }
    }

    private void songDialog(SongDialogViewBuilder.Mode mode, Song song) {
        songDialogModel.setProperties(song);
        Dialog<Song> dialog = new Dialog<>();
        dialog.setTitle(mode == SongDialogViewBuilder.Mode.CREATE ? "Create Song" : "Edit Song");

        SongDialogViewBuilder builder = new SongDialogViewBuilder(
                songDialogModel,
                songModel.getObservableGenres(),
                this::chooseFile
        );
        Region content = builder.build();
        dialog.getDialogPane().setContent(content);

        // Dialog buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        Node okBtn = dialog.getDialogPane().lookupButton(ButtonType.OK);
        songDialogModel.isValidInputProperty().bind(Bindings.createBooleanBinding(this::isDataValid, songDialogModel.getBindableProperties()));
        okBtn.disableProperty().bind(songDialogModel.isValidInputProperty().not());

        // Handle pressing the dialog buttons
        dialog.setResultConverter(dialogBtn -> {
            if (dialogBtn == ButtonType.OK) {
                if (mode == SongDialogViewBuilder.Mode.CREATE) {
                    return createNewSongFromModel(songDialogModel);
                } else {
                    return updateSongFromModel(songDialogModel);
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void displayError(Throwable t) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(t.getMessage());
        alert.showAndWait();
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

            songDialogModel.setArtist(artist != null ? artist : "");
            songDialogModel.setTitle(title != null ? title : "");
            songDialogModel.setDuration(millis);
        });
    }

    private boolean isDataValid() {
        return !songDialogModel.getTitle().trim().isEmpty() &&
               !songDialogModel.getArtist().trim().isEmpty() &&
               !songDialogModel.getGenre().trim().isEmpty() &&
               !songDialogModel.getDuration().trim().isEmpty() &&
               !songDialogModel.getFilePath().trim().isEmpty();
    }

    private Song createNewSongFromModel(SongDialogModel model) {
        try {
            String title = model.getTitle();
            String artist = model.getArtist();
            String genre = model.getGenre();
            int duration = new TimeStringConverter().fromString(model.getDuration());
            String filePath = model.getFilePath();

            Song newSong = new Song(-1, title, artist, genre, duration, filePath);
            songModel.createNewSong(newSong);
            model.reset();
            return newSong;
        } catch (Exception e) {
            displayError(e);
            return null;
        }
    }

    private Song updateSongFromModel(SongDialogModel model) {
        try {
            int id = model.getId();
            String title = model.getTitle();
            String artist = model.getArtist();
            String genre = model.getGenre();
            int duration = new TimeStringConverter().fromString(model.getDuration());
            String filePath = model.getFilePath();

            Song song = new Song(id, title, artist, genre, duration, filePath);
            songModel.updateSong(song);
            model.reset();
            tblSongs.refresh();
            return song;
        } catch (Exception e) {
            displayError(e);
            return null;
        }
    }
}
