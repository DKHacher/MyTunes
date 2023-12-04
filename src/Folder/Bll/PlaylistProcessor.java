package Folder.Bll;

import Folder.Be.Playlist;
import Folder.Be.Playlist;
import Folder.Dal.IPlaylistDataAccess;
import Folder.Dal.IPlaylistDataAccess;
import Folder.Dal.PlaylistDAO_DB;
import Folder.Dal.PlaylistDAO_DB;

import java.io.IOException;
import java.util.List;

public class PlaylistProcessor {
    private IPlaylistDataAccess listDAO;

    public PlaylistProcessor() throws IOException {
        listDAO = new PlaylistDAO_DB();
    }

    public List<Playlist> getAllPlaylists() throws Exception {
        return listDAO.getAllPlaylists();
    }


    public Playlist createNewPlaylist(Playlist newPlaylist) throws Exception {
        return listDAO.createPlaylist(newPlaylist);
    }
}
