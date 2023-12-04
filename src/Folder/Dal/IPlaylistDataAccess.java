package Folder.Dal;

import Folder.Be.Playlist;
import Folder.Be.Song;

import java.util.List;

public interface IPlaylistDataAccess {
    List<Playlist> getAllPlaylists() throws Exception;

    Playlist createPlaylist(Playlist playlist) throws Exception;

    public List<Song> getAllSongsInPlaylist(int id) throws Exception;
}
