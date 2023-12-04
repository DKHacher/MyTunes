package Folder.Gui.model;

import Folder.Be.Song;
import Folder.Bll.SongProcessor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Optional;

public class SongModel {
    private ObservableList<Song> songsToBeViewed;
    private ObservableList<Song> playbackSongs;
    private ObservableList<String> genres;
    private SongProcessor songProcessor;

    public SongModel() throws Exception {
        songProcessor = new SongProcessor();
        songsToBeViewed = FXCollections.observableArrayList();
        songsToBeViewed.addAll(songProcessor.getAllSongs());

        genres = FXCollections.observableArrayList();
        genres.addAll(songProcessor.getAllGenres());

        playbackSongs = FXCollections.observableArrayList();
        playbackSongs.addAll(songsToBeViewed);
    }

    public ObservableList<Song> getObservableSongs() {
        return songsToBeViewed;
    }

    public ObservableList<String> getObservableGenres() {
        return genres;
    }

    public ObservableList<Song> getPlaybackSongs() {
        return playbackSongs;
    }

    public void updateGenres() throws Exception {
        genres.clear();
        genres.addAll(songProcessor.getAllGenres());
    }

    public void createNewSong(Song song) throws Exception {
        Song s = songProcessor.createNewSong(song);
        songsToBeViewed.add(s);
    }

    public void updateSong(Song updatedSong) throws Exception {
        songProcessor.updateSong(updatedSong);

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
        songProcessor.deleteSong(song);
        songsToBeViewed.remove(song);
    }

    public void filterSongs(String searchTexT) throws Exception {
        List<Song> filteredSongs = songProcessor.filterSongs(searchTexT);
        songsToBeViewed.clear();
        songsToBeViewed.addAll(filteredSongs);
    }
}
