package Folder.Gui.model;

import Folder.Be.Song;
import Folder.Bll.DataProcessor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Optional;

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

    public void updateSong(Song updatedSong) throws Exception {
        dataProcessor.updateSong(updatedSong);

        Optional<Song> matchingSong = songsToBeViewed.stream().filter(s -> s.getId() == updatedSong.getId()).findFirst();
        if (matchingSong.isPresent()) {
            Song song = matchingSong.get();

            song.setTitle(updatedSong.getTitle());
            song.setArtist(updatedSong.getArtist());
            song.setGenre(updatedSong.getGenre());
            song.setDuration(updatedSong.getDuration());
            song.setFilePath(updatedSong.getFilePath());
        }
    }

    public void deleteSong(Song song) throws Exception {
        dataProcessor.deleteSong(song);
        songsToBeViewed.remove(song);
    }

    public void filterSongs(String searchTexT) throws Exception {
        List<Song> filteredSongs = dataProcessor.filterSongs(searchTexT);
        songsToBeViewed.clear();
        songsToBeViewed.addAll(filteredSongs);
    }
}
