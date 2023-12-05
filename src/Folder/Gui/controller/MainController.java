package Folder.Gui.controller;

import Folder.Be.Playlist;
import Folder.Be.Song;
import Folder.Gui.model.PlaylistDialogModel;
import Folder.Gui.model.PlaylistModel;
import Folder.Gui.model.SongDialogModel;
import Folder.Gui.model.SongModel;
import Folder.Gui.util.PlaybackHandler;
import Folder.Gui.util.TimeStringConverter;
import Folder.Gui.view.PlaylistDialogViewBuilder;
import Folder.Gui.view.SongDialogViewBuilder;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

public class MainController {
    @FXML private TableView tblPlaylists;
    @FXML private TableColumn<Playlist, String> colPlistName;
    @FXML private TableColumn<Playlist, String> colPlistSongs;
    @FXML private TableColumn<Playlist, String> colPlistTime;

    @FXML private TableView tblSongs;
    @FXML private TableColumn<Song, String> colTitle;
    @FXML private TableColumn<Song, String> colArtist;
    @FXML private TableColumn<Song, String> colGenre;
    @FXML private TableColumn<Song, String> colDuration;

    @FXML private TextField searchField;
    @FXML private Button btnPlay;
    @FXML private ImageView playPauseImageView;
    @FXML private Slider volumeSlider;
    @FXML private Label currentPlayingLbl;
    @FXML private Slider playbackSlider;
    @FXML private Label curTimeLbl;
    @FXML private Label totalDurLbl;

    private final SongDialogModel songDialogModel;
    private final PlaylistDialogModel playlistDialogModel;
    private final PlaybackHandler playbackHandler;
    private SongModel songModel;
    private PlaylistModel playlistModel;


    public MainController() {
        songDialogModel = new SongDialogModel();
        playlistDialogModel = new PlaylistDialogModel();
        playbackHandler = new PlaybackHandler();

        try {
            songModel = new SongModel();
            playlistModel = new PlaylistModel();
        } catch (Exception e) {
            displayError(e);
            e.printStackTrace();
        }
    }
    /*
    * Frederik:
    * I split the initialize method into songInitialize and playlistInitialize.
    * because having both song and playlist in one initialize felt it was too big.
    * maybe split more parts into their own segments, like Volume and CurrentlyPlaying initializers
    * */
    public void initialize() {
        songInitialize();
        playlistInitialize();
    }

    public void songInitialize(){
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colDuration.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(new TimeStringConverter().toString(cellData.getValue().getDuration())));

        tblSongs.setItems(songModel.getObservableSongs());

        volumeSlider.valueProperty().bindBidirectional(playbackHandler.volumeProperty());
        currentPlayingLbl.textProperty().bind(playbackHandler.currentPlayingSongProperty());


        playbackSlider.valueProperty().bindBidirectional(playbackHandler.currentSongPositionProperty());

        playbackSlider.setOnMousePressed(evt -> playbackHandler.pause());
        playbackSlider.setOnMouseReleased(evt -> {
            double newPos = playbackSlider.getValue();
            double totalDur = playbackHandler.getTotalDuration();
            playbackHandler.seek((newPos / 100.0) * totalDur);
        });

        curTimeLbl.textProperty().bind(Bindings.createStringBinding(() -> {
            double curTime = playbackHandler.getCurrentTime();
            return new TimeStringConverter().toString((int) (curTime * 1000));
        }, playbackHandler.currentTimeProperty().asObject()));

        totalDurLbl.textProperty().bind(Bindings.createStringBinding(() -> {
            double totalDur = playbackHandler.getTotalDuration();
            return new TimeStringConverter().toString((int) (totalDur * 1000));
        }, playbackHandler.totalDurationProperty().asObject()));
    }

    public void playlistInitialize(){
        colPlistName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPlistSongs.setCellValueFactory(new PropertyValueFactory<>("amountOfSongs"));
        colPlistTime.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(new TimeStringConverter().toString(cellData.getValue().getPlaylistDuration())));

        tblPlaylists.setItems(playlistModel.getObservablePlaylists());
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

    @FXML
    private void handleSearch(KeyEvent event) {
        String searchText = searchField.getText();
        try {
            songModel.filterSongs(searchText);
        } catch (Exception e) {
            displayError(e);
        }
    }

    @FXML
    private void playSong(ActionEvent event) {
        Song selectedSong = (Song) tblSongs.getSelectionModel().getSelectedItem();

        if (!playbackHandler.isPlaying() && selectedSong != null) {
            togglePlayPauseIcon(true);
            playbackHandler.play(selectedSong);
        } else if (playbackHandler.isPlaying()) {
            togglePlayPauseIcon(false);
            playbackHandler.pause();
        }
    }

    @FXML
    private void nextSong(ActionEvent event) {
        if (playbackHandler.isPlaying()) {
            Song curSong = playbackHandler.getCurrentSong();
            if (curSong != null) {
                ObservableList<Song> playbackSongs = songModel.getPlaybackSongs();
                int curIndex = playbackSongs.indexOf(curSong);

                if (curIndex >= 0) {
                    int nextIndex = curIndex + 1;
                    if (nextIndex < playbackSongs.size()) {
                        playbackHandler.play(playbackSongs.get(nextIndex));
                    }
                }
            }
        }
    }

    @FXML
    private void prevSong(ActionEvent event) {
        if (playbackHandler.isPlaying()) {
            Song curSong = playbackHandler.getCurrentSong();
            if (curSong != null) {
                ObservableList<Song> playbackSongs = songModel.getPlaybackSongs();
                int curIndex = playbackSongs.indexOf(curSong);

                if (curIndex >= 0) {
                    int prevIndex = curIndex - 1;
                    if (prevIndex >= 0) {
                        playbackHandler.play(playbackSongs.get(prevIndex));
                    }
                }
            }
        }
    }

    @FXML
    private void muteVolume(ActionEvent event) {
        if (playbackHandler.isMuted()) {
            playbackHandler.unmute();
        } else {
            playbackHandler.mute();
        }
    }

    private void togglePlayPauseIcon(boolean isPlaying) {
        if (isPlaying) {
            playPauseImageView.setImage(new Image("/Images/pause.png"));
        } else {
            playPauseImageView.setImage(new Image("/Images/one-right.png"));
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

    @FXML
    private void newPlaylist(ActionEvent actionEvent) {
        try {
            playlistDialog(PlaylistDialogViewBuilder.Mode.CREATE, null);
        } catch (Exception e) {
            displayError(e);
            e.printStackTrace();
        }
    }

    @FXML
    private void editPlaylist(ActionEvent actionEvent) {
        Playlist selectedPlaylist = (Playlist) tblPlaylists.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null) {
            try {
                playlistDialog(PlaylistDialogViewBuilder.Mode.EDIT, selectedPlaylist);
                playlistDialogModel.reset();
            } catch (Exception e) {
                displayError(e);
                e.printStackTrace();
            }
        }
    }

    @FXML//will do this one another day, just copied delete song, -frederik
    private void deletePlaylist(ActionEvent actionEvent) {
        /*Song selectedSong = (Song) tblSongs.getSelectionModel().getSelectedItem();

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
        }*/
    }
    private void playlistDialog(PlaylistDialogViewBuilder.Mode mode, Playlist playlist) {
        playlistDialogModel.setProperties(playlist);
        Dialog<Playlist> dialog = new Dialog<>();
        dialog.setTitle(mode == PlaylistDialogViewBuilder.Mode.CREATE ? "Create Playlist" : "Edit Playlist");

        PlaylistDialogViewBuilder builder = new PlaylistDialogViewBuilder(
                playlistDialogModel
        );
        Region content = builder.build();
        dialog.getDialogPane().setContent(content);

        // Dialog buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        Node okBtn = dialog.getDialogPane().lookupButton(ButtonType.OK);
        //playlistDialogModel.isValidInputProperty().bind(Bindings.createBooleanBinding(this::isDataValid, songDialogModel.getBindableProperties()));
        //okBtn.disableProperty().bind(playlistDialogModel.isValidInputProperty().not());

        // Handle pressing the dialog buttons
        dialog.setResultConverter(dialogBtn -> {
            if (dialogBtn == ButtonType.OK) {
                if (mode == PlaylistDialogViewBuilder.Mode.CREATE) {
                    return createNewPlaylistFromModel(playlistDialogModel);
                } else {
                    return updatePlaylistFromModel(playlistDialogModel);
                }
            }
            return null;
        });

        dialog.showAndWait();
    }
    private Playlist createNewPlaylistFromModel(PlaylistDialogModel model) {
        try {
            String name = model.getName();

            Playlist newPlaylist = new Playlist(-1, name, new ArrayList<>());
            playlistModel.createNewPlaylist(newPlaylist);
            model.reset();
            return newPlaylist;
        } catch (Exception e) {
            displayError(e);
            return null;
        }
    }

    private Playlist updatePlaylistFromModel(PlaylistDialogModel model) {
        try {
            int id = model.getId();
            String name = model.getName();


            Playlist playlist = new Playlist(id, name, new ArrayList<>());
            playlistModel.updatePlaylist(playlist);
            model.reset();
            tblPlaylists.refresh();
            return playlist;
        } catch (Exception e) {
            displayError(e);
            return null;
        }
    }
}
