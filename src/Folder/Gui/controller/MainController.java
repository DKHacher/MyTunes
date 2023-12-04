package Folder.Gui.controller;

import Folder.Be.Song;
import Folder.Gui.model.SongModel;
import Folder.Gui.util.TimeFilter;
import Folder.Gui.util.TimeStringConverter;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class MainController {
    @FXML private TableView tblSongs;
    @FXML private TableColumn<Song, String> colTitle;
    @FXML private TableColumn<Song, String> colArtist;
    @FXML private TableColumn<Song, String> colGenre;
    @FXML private TableColumn<Song, String> colDuration;

    @FXML private Button btnNew;

    private SongModel songModel;

    public MainController() {
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
        colDuration.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(formatDuration(cellData.getValue().getDuration())));

        tblSongs.setItems(songModel.getObservableSongs());

    }

    private void displayError(Throwable t) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(t.getMessage());
        alert.showAndWait();
    }

    private String formatDuration(int millis) {
        int minutes = (millis / 1000) / 60;
        int seconds = (millis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @FXML
    private void newSong(ActionEvent event) {
        try {
            //model.createNewSong(new Song(-1, "test", "test", "test", 0, ""));
            songModel.updateGenres();
            createInputDialog("New Song", songModel.getObservableGenres());
        } catch (Exception e) {
            displayError(e);
            e.printStackTrace();
        }
    }

    private Optional<Song> createInputDialog(String dialogTitle, List<String> existingGenres) {
        TimeFilter timeFilter = new TimeFilter();
        TimeStringConverter timeConverter = new TimeStringConverter();

        Dialog<Song> dialog = new Dialog<>();
        dialog.setTitle(dialogTitle);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField fieldTitle = new TextField();
        TextField fieldArtist = new TextField();
        TextField fieldDuration = new TextField();
        TextField fieldFilePath = new TextField();

        Label lblTitle = new Label("Title:");
        Label lblArtist = new Label("Artist:");
        Label lblGenre = new Label("Genre:");
        Label lblDuration = new Label("Duration:");
        Label lblFilePath = new Label("File:");

        Button btnChooseFile = new Button("Choose...");
        btnChooseFile.setOnAction(evt -> {
            FileChooser chooser = new FileChooser();

            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Music Files", "*.mp3", "*.wav");
            chooser.getExtensionFilters().add(filter);

            File selectedFile = chooser.showOpenDialog(null);
            if (selectedFile != null) {
                fieldFilePath.setText(selectedFile.getAbsolutePath());

                Media media = new Media(selectedFile.toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);

                mediaPlayer.setOnReady(() -> {
                    String artist = (String) media.getMetadata().get("artist");
                    String title = (String) media.getMetadata().get("title");
                    Duration duration = media.getDuration();
                    int millis = (int) duration.toMillis();

                    fieldTitle.setText(title != null ? title : "");
                    fieldArtist.setText(artist != null ? artist : "");
                    fieldDuration.setText(timeConverter.toString(millis));
                    System.out.println(media.getMetadata());
                });
            }
        });

        ComboBox<String> comboGenre = new ComboBox<>();
        comboGenre.getItems().addAll(existingGenres);
        comboGenre.setEditable(true);

        comboGenre.getEditor().textProperty().addListener((obs, ov, nv) -> {
/*            if (!nv.trim().isEmpty()) {
                comboGenre.getSelectionModel().select(nv);
            }*/

            if (!nv.equals(ov) && !nv.equals(comboGenre.getSelectionModel().getSelectedItem())) {
                List<String> filteredList = existingGenres.stream()
                        .filter(genre -> genre.toLowerCase().contains(nv.toLowerCase()))
                        .toList();

                if (!comboGenre.getItems().equals(filteredList)) {
                    comboGenre.getItems().setAll(filteredList);
                }
            }
        });

        TextFormatter<Integer> timeFormatter = new TextFormatter<>(timeConverter, 0, timeFilter);
        fieldDuration.setTextFormatter(timeFormatter);

        grid.add(lblTitle, 0, 0);
        grid.add(fieldTitle, 1, 0);

        grid.add(lblArtist, 0, 1);
        grid.add(fieldArtist, 1, 1);

        grid.add(lblGenre, 0, 2);
        grid.add(comboGenre, 1, 2);

        grid.add(lblDuration, 0, 3);
        grid.add(fieldDuration, 1, 3);

        grid.add(lblFilePath, 0, 4);
        grid.add(fieldFilePath, 1, 4);
        grid.add(btnChooseFile, 2, 4);

        dialog.getDialogPane().setContent(grid);

        // Set up OK button
        ButtonType btnTypeOk = ButtonType.OK;
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, btnTypeOk);

        Node okBtn = dialog.getDialogPane().lookupButton(btnTypeOk);
        okBtn.setDisable(true); // Initially disable OK button

        Runnable validateInputs = () -> {
            boolean isGenreValid = !comboGenre.getEditor().getText().trim().isEmpty() ||
                    !comboGenre.getSelectionModel().isEmpty();
            okBtn.setDisable(
                    fieldTitle.getText().trim().isEmpty() ||
                            fieldArtist.getText().trim().isEmpty() ||
                            !isGenreValid ||
                            fieldDuration.getText().trim().isEmpty() ||
                            fieldFilePath.getText().trim().isEmpty()
            );
        };

        fieldTitle.textProperty().addListener(obs -> validateInputs.run());
        fieldArtist.textProperty().addListener(obs -> validateInputs.run());
        comboGenre.valueProperty().addListener(obs -> validateInputs.run());
        fieldDuration.textProperty().addListener(obs -> validateInputs.run());
        fieldFilePath.textProperty().addListener(obs -> validateInputs.run());

        dialog.setResultConverter(dialogBtn -> {
            if (dialogBtn == ButtonType.OK) {
                try {
                    String title = fieldTitle.getText();
                    String artist = fieldArtist.getText();
                    String genre = comboGenre.getValue();
                    int duration = timeFormatter.getValue();
                    String filePath = fieldFilePath.getText();
                    songModel.createNewSong(new Song(-1, title, artist, genre, duration, filePath));
                    return new Song(-1, title, artist, genre, duration, filePath);
                } catch (Exception e) {
                    displayError(e);
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }
}
