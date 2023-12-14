package Folder.Gui.model;

import Folder.Be.Playlist;
import Folder.Bll.PlaylistProcessor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Optional;

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

    public void updatePlaylist(Playlist updatedPlaylist) throws Exception {
        PlaylistProcessor.updatePlaylist(updatedPlaylist);

        Optional<Playlist> matchingPlaylist = PlaylistsToBeViewed.stream().filter(s -> s.getId() == updatedPlaylist.getId()).findFirst();
        if (matchingPlaylist.isPresent()) {
            Playlist playlist = matchingPlaylist.get();

            playlist.setName(updatedPlaylist.getName());
        }
    }

    public void deletePlaylist(Playlist playlist) throws Exception {
        PlaylistProcessor.deletePlaylist(playlist);
        PlaylistsToBeViewed.remove(playlist);
    }

    public void UpdatePlaylistSongs(Playlist playlist) throws Exception {
        PlaylistProcessor.updatePlaylistSongs(playlist);
    }


}
