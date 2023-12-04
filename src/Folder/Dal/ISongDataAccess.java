package Folder.Dal;

import Folder.Be.Song;

import java.util.List;

public interface ISongDataAccess {
    List<Song> getAllSongs() throws Exception;
    List<String> getAllGenres() throws Exception;
    Song createSong(Song song) throws Exception;
    public void updateSong(Song song) throws Exception;
    public void deleteSong(Song song) throws Exception;
    public List<Song> filterSongs(String searchText) throws Exception;
}
