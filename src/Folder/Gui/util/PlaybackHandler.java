package Folder.Gui.util;

import Folder.Be.Song;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class PlaybackHandler {
    private MediaPlayer currentPlayer;
    private Song currentSong;

    public void play(Song song) {
        if (currentPlayer != null && song.equals(currentSong)) {
            currentPlayer.play();
        } else {
            if (currentPlayer != null) {
                currentPlayer.stop();
            }

            Media media = new Media(song.getFile().toURI().toString());
            currentPlayer = new MediaPlayer(media);
            currentSong = song;
            currentPlayer.play();
        }
    }
}
