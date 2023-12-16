package Folder.Gui.model;

import Folder.Be.Song;
import Folder.Gui.util.TimeStringConverter;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

public class SongDialogModel {
    private final IntegerProperty id = new SimpleIntegerProperty(-1);
    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty artist = new SimpleStringProperty("");
    private final StringProperty genre = new SimpleStringProperty("");
    private final StringProperty duration = new SimpleStringProperty("");
    private final StringProperty filePath = new SimpleStringProperty("");
    private final BooleanProperty isValidInput = new SimpleBooleanProperty(false);
    private final StringProperty fileAbsolutePath = new SimpleStringProperty("");

    public SongDialogModel() {
        reset();
    }

    public void setProperties(Song song) {
        if (song != null) {
            setId(song.getId());
            setTitle(song.getTitle());
            setArtist(song.getArtist());
            setGenre(song.getGenre());
            setDuration(song.getDuration());
            setFilePath(song.getFilePath());
        }
    }

    public Observable[] getBindableProperties() {
        return new Observable[] {
                idProperty(),
                titleProperty(),
                artistProperty(),
                genreProperty(),
                durationProperty(),
                filePathProperty()
        };
    }

    public void reset() {
        setId(-1);
        setTitle("");
        setArtist("");
        setGenre("");
        setDuration(0);
        setFilePath("");
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getArtist() {
        return artist.get();
    }

    public StringProperty artistProperty() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist.set(artist);
    }

    public String getGenre() {
        return genre.get();
    }

    public StringProperty genreProperty() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre.set(genre);
    }

    public String getDuration() {
        return duration.get();
    }

    public StringProperty durationProperty() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration.set(new TimeStringConverter().toString(duration));
    }

    public String getFilePath() {
        return filePath.get();
    }

    public StringProperty filePathProperty() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath.set(filePath);
    }

    public boolean getIsValidInput() {
        return isValidInput.get();
    }

    public BooleanProperty isValidInputProperty() {
        return isValidInput;
    }

    public void setIsValidInput(boolean isValidInput) {
        this.isValidInput.set(isValidInput);
    }

    public String getFileAbsolutePath() {
        return fileAbsolutePath.get();
    }

    public StringProperty fileAbsolutePathProperty() {
        return fileAbsolutePath;
    }

    public void setFileAbsolutePath(String fileAbsolutePath) {
        this.fileAbsolutePath.set(fileAbsolutePath);
    }
}
