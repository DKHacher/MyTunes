package Folder.Gui.model;

import Folder.Be.Playlist;
import Folder.Bll.PlaylistProcessor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PlaylistModel {
    private ObservableList<Playlist> PlaylistsToBeViewed;
    private PlaylistProcessor PlaylistProcessor;

    public PlaylistModel() throws Exception {
        PlaylistProcessor = new PlaylistProcessor();
        PlaylistsToBeViewed = FXCollections.observableArrayList();
        PlaylistsToBeViewed.addAll(PlaylistProcessor.getAllPlaylists());
    }

    public ObservableList<Playlist> getObservablePlaylists() {
        return PlaylistsToBeViewed;
    }


    public void createNewPlaylist(Playlist Playlist) throws Exception {
        Playlist s = PlaylistProcessor.createNewPlaylist(Playlist);
        PlaylistsToBeViewed.add(s);
    }
}
