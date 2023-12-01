package Folder.Dal;

import Folder.Be.Song;

import java.util.List;

public interface ISongDataAccess {
    List<Song> getAllSongs() throws Exception;
    List<String> getAllGenres() throws Exception;
    Song createSong(Song song) throws Exception;
}
