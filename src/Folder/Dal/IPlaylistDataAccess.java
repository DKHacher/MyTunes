package Folder.Dal;

import Folder.Be.Playlist;

import java.util.List;

public interface IPlaylistDataAccess {
    List<Playlist> getAllPlaylists() throws Exception;

    Playlist createPlaylist(Playlist playlist) throws Exception;
}
