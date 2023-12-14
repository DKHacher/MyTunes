package Folder.Dal;

import Folder.Be.Playlist;
import Folder.Be.Song;

import java.util.List;

public interface IPlaylistDataAccess {
    List<Playlist> getAllPlaylists() throws Exception;
    Playlist createPlaylist(Playlist playlist) throws Exception;
    public List<Song> getAllSongsInPlaylist(int id) throws Exception;
    public void updatePlaylist(Playlist playlist) throws Exception;
    public void deletePlaylist(Playlist playlist) throws Exception;
    public void updatePlaylistSongs(Playlist playlist) throws Exception;
}
