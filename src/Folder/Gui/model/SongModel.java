package Folder.Gui.model;

import Folder.Be.Song;
import Folder.Bll.DataProcessor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SongModel {
    private ObservableList<Song> songsToBeViewed;
    private ObservableList<String> genres;
    private DataProcessor dataProcessor;

    public SongModel() throws Exception {
        dataProcessor = new DataProcessor();
        songsToBeViewed = FXCollections.observableArrayList();
        songsToBeViewed.addAll(dataProcessor.getAllSongs());

        genres = FXCollections.observableArrayList();
        genres.addAll(dataProcessor.getAllGenres());
    }

    public ObservableList<Song> getObservableSongs() {
        return songsToBeViewed;
    }

    public ObservableList<String> getObservableGenres() {
        return genres;
    }

    public void updateGenres() throws Exception {
        genres.clear();
        genres.addAll(dataProcessor.getAllGenres());
    }

    public void createNewSong(Song song) throws Exception {
        Song s = dataProcessor.createNewSong(song);
        songsToBeViewed.add(s);
    }
}
