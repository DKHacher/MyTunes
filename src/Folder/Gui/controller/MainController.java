package Folder.Gui.controller;

import Folder.Be.Playlist;
import Folder.Be.Song;
import Folder.Common.SongPlaybackException;
import Folder.Gui.model.*;
import Folder.Gui.util.DialogBuilder;
import Folder.Gui.util.PlaybackHandler;
import Folder.Gui.util.TimeStringConverter;
import Folder.Gui.view.PlaylistDialogViewBuilder;
import Folder.Gui.view.SongDialogViewBuilder;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

public class MainController {
    @FXML private ListView lstSongsInPlaylist;
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
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;
    @FXML private ImageView playPauseImageView;
    @FXML private Slider volumeSlider;
    @FXML private Label currentPlayingLbl;
    @FXML private Slider playbackSlider;
    @FXML private Label curTimeLbl;
    @FXML private Label totalDurLbl;

    private final SongDialogModel songDialogModel;
    private final PlaylistDialogModel playlistDialogModel;
    private final PlaybackHandler playbackHandler;
    private final PlaybackModel playbackModel;
    private SongModel songModel;
    private PlaylistModel playlistModel;


    public MainController() {
        songDialogModel = new SongDialogModel();
        playlistDialogModel = new PlaylistDialogModel();
        playbackModel = new PlaybackModel();
        playbackHandler = new PlaybackHandler(playbackModel);

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
        playbackInitialize();
    }

    public void playbackInitialize() {
        initializePlayPauseBinding();

        AtomicBoolean isDragging = new AtomicBoolean(false);
        playbackSlider.setOnMousePressed(evt -> {
            isDragging.set(true);
        });

        playbackSlider.setOnMouseReleased(evt -> {
            playbackHandler.seek(playbackSlider.getValue());
            isDragging.set(false);
        });

        playbackSlider.valueProperty().addListener((obs, ov, nv) -> {
            if (isDragging.get()) {
                double newPosition = nv.doubleValue();
                double totalDuration = playbackModel.getTotalDuration();
                double newTime = newPosition / 100.0 * totalDuration;
                updateCurrentTimeLabel(newTime);
            } else {
                playbackModel.setCurrentPosition(nv.doubleValue());
            }
        });

        playbackModel.currentPositionProperty().addListener((obs, ov, nv) -> {
            if (!isDragging.get()) {
                playbackSlider.setValue(nv.doubleValue());
            }
        });

        playbackModel.currenTimeProperty().addListener((obs, ov, nv) -> {
            if (!isDragging.get()) {
                updateCurrentTimeLabel(nv.doubleValue());
            }
        });

        totalDurLbl.textProperty().bind(Bindings.createStringBinding(() -> {
            double totalDur = playbackModel.getTotalDuration();
            return new TimeStringConverter().toString((int) (totalDur * 1000));
        }, playbackModel.totalDurationProperty().asObject()));

        volumeSlider.valueProperty().bindBidirectional(playbackModel.volumeProperty());

        volumeSlider.valueProperty().addListener((obs, ov, nv) -> {
            playbackHandler.setVolume(nv.doubleValue());
        });

        playbackModel.currentSongProperty().addListener((obs, ov, nv) ->
            Platform.runLater(() -> {
                if (nv != null) {
                    currentPlayingLbl.setText(nv.getTitle() + " by " + nv.getArtist());
                } else {
                    currentPlayingLbl.setText("(None) is playing");
                }
            })
        );

        playbackHandler.setQueue(songModel.getObservableSongs());
    }

    private void updateCurrentTimeLabel(double currentTimeInSeconds) {
        curTimeLbl.setText(new TimeStringConverter().toString((int) (currentTimeInSeconds * 1000)));
    }

    public void songInitialize(){
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colDuration.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(new TimeStringConverter().toString(cellData.getValue().getDuration())));

        tblSongs.setItems(songModel.getObservableSongs());

        // Used to disable/enable buttons when selecting other songs & the playing song in the TableView.
        tblSongs.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            Song selectedSong = (Song) tblSongs.getSelectionModel().getSelectedItem();
            Song currentSong = playbackModel.getCurrentSong();

            btnEdit.setDisable(selectedSong != null && selectedSong.equals(currentSong));
            btnDelete.setDisable(selectedSong != null && selectedSong.equals(currentSong));
        });

        // Used to disable buttons immediately when playing song that is selected.
        playbackModel.currentSongProperty().addListener((obs, ov, nv) -> {
            Song selectedSong = (Song) tblSongs.getSelectionModel().getSelectedItem();

            btnEdit.setDisable(selectedSong != null && selectedSong.equals(nv));
            btnDelete.setDisable(selectedSong != null && selectedSong.equals(nv));
        });

    }


    public void playlistInitialize(){
        colPlistName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPlistSongs.setCellValueFactory(new PropertyValueFactory<>("amountOfSongs"));
        colPlistTime.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(new TimeStringConverter().toString(cellData.getValue().getPlaylistDuration())));

        tblPlaylists.setItems(playlistModel.getObservablePlaylists());
    }

    public void playlistSongsRefresh(){
        Playlist selectedPlaylist = (Playlist) tblPlaylists.getSelectionModel().getSelectedItem();
        lstSongsInPlaylist.getItems().clear();
        if (selectedPlaylist != null){
            lstSongsInPlaylist.getItems().addAll(selectedPlaylist.getSongsInPlaylistAsString());
        }
    }

    @FXML
    private void newSong(ActionEvent event) {
        try {
            songModel.updateGenres();
            songDialog(null);
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
                songDialog(selectedSong);
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
        Task<Void> playTask = new Task<>() {
            @Override
            protected Void call() throws SongPlaybackException {
                if (!playbackModel.getIsPlaying()) {
                    if (selectedSong != null) {
                        playbackHandler.play(selectedSong);
                    } else {
                        playbackHandler.playQueue();
                    }
                } else {
                    playbackHandler.pause();
                }
                return null;
            }
        };

        playTask.setOnFailed(e -> {
            Throwable exception = playTask.getException();
            displayError(exception);
        });

        new Thread(playTask).start();

    }

    private void initializePlayPauseBinding() {
        playbackModel.isPlayingProperty().addListener((obs, wasPlaying, isPlaying) -> {
            togglePlayPauseIcon(isPlaying);
        });
    }

    private void togglePlayPauseIcon(boolean isPlaying) {
        if (isPlaying) {
            playPauseImageView.setImage(new Image("/Images/pause.png"));
        } else {
            playPauseImageView.setImage(new Image("/Images/one-right.png"));
        }
    }

    @FXML
    private void nextSong(ActionEvent event) {
        playbackHandler.nextSong();
    }

    @FXML
    private void prevSong(ActionEvent event) {
        playbackHandler.previousSong();
    }

    @FXML
    private void muteVolume(ActionEvent event) {
        if (playbackModel.getIsMuted()) {
            playbackHandler.unmute();
        } else {
            playbackHandler.mute();
        }
    }

    private void songDialog(Song song) {
        String dialogTitle = (song == null) ? "Create Song" : "Edit Song";

        Dialog<Song> songDialog = new DialogBuilder<Song>(new SongDialogController(song, songModel.getObservableGenres()))
                .withTitle(dialogTitle)
                .addButtonTypes(ButtonType.OK, ButtonType.CANCEL)
                .build();

        songDialog.showAndWait().ifPresent(response -> handleSongResponse(song, response));
    }

    private void handleSongResponse(Song existingSong, Song responseSong) {
        try {
            if (existingSong == null) {
                songModel.createNewSong(responseSong);
            } else {
                songModel.updateSong(responseSong);
            }
            tblSongs.refresh();
        } catch (Exception e) {
            displayError(e);
        }
    }

    private void displayError(Throwable t) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(t.getMessage());
        alert.showAndWait();
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

    @FXML
    private void deletePlaylist(ActionEvent actionEvent) {
        Playlist selectedPlaylist = (Playlist) tblPlaylists.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null) {
            try {
                playlistModel.deletePlaylist(selectedPlaylist);
                tblPlaylists.refresh();

            } catch (Exception e) {
                displayError(e);
                e.printStackTrace();
            }
        }
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

    public void tblPlaylistMouseClick(MouseEvent mouseEvent) {
        playlistSongsRefresh();
    }
}
