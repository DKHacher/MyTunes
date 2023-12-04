package Folder.Gui.model;

import Folder.Be.Song;
import Folder.Bll.SongProcessor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SongModel {
    private ObservableList<Song> songsToBeViewed;
    private ObservableList<String> genres;
    private SongProcessor songProcessor;

    public SongModel() throws Exception {
        songProcessor = new SongProcessor();
        songsToBeViewed = FXCollections.observableArrayList();
        songsToBeViewed.addAll(songProcessor.getAllSongs());

        genres = FXCollections.observableArrayList();
        genres.addAll(songProcessor.getAllGenres());
    }

    public ObservableList<Song> getObservableSongs() {
        return songsToBeViewed;
    }

    public ObservableList<String> getObservableGenres() {
        return genres;
    }

    public void updateGenres() throws Exception {
        genres.clear();
        genres.addAll(songProcessor.getAllGenres());
    }

    public void createNewSong(Song song) throws Exception {
        Song s = songProcessor.createNewSong(song);
        songsToBeViewed.add(s);
    }
}
