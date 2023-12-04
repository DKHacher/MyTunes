package Folder.Bll;

import Folder.Be.Song;
import Folder.Dal.ISongDataAccess;
import Folder.Dal.SongDAO_DB;

import java.io.IOException;
import java.util.List;

public class SongProcessor {
    private ISongDataAccess musicDAO;

    public SongProcessor() throws IOException {
        musicDAO = new SongDAO_DB();
    }

    public List<Song> getAllSongs() throws Exception {
        return musicDAO.getAllSongs();
    }

    public List<String> getAllGenres() throws Exception {
        return musicDAO.getAllGenres();
    }

    public Song createNewSong(Song newSong) throws Exception {
        return musicDAO.createSong(newSong);
    }

    public void updateSong(Song song) throws Exception {
        musicDAO.updateSong(song);
    }

    public void deleteSong(Song song) throws Exception {
        musicDAO.deleteSong(song);
    }

    public List<Song> filterSongs(String searchText) throws Exception {
        return musicDAO.filterSongs(searchText);
    }
}
