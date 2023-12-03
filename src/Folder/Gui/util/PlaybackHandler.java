package Folder.Gui.util;

import Folder.Be.Song;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class PlaybackHandler {
    private MediaPlayer currentPlayer;
    private Song currentSong;
    private boolean isPlaying;

    public PlaybackHandler() {
        isPlaying = false;
    }

    public void play(Song song) {
        if (currentPlayer != null && song.equals(currentSong)) {
            currentPlayer.play();
            isPlaying = true;
        } else {
            if (currentPlayer != null) {
                currentPlayer.stop();
                isPlaying = false;
            }

            Media media = new Media(song.getFile().toURI().toString());
            currentPlayer = new MediaPlayer(media);
            currentSong = song;
            currentPlayer.play();
            isPlaying = true;
        }
    }

    public void pause() {
        if (currentPlayer != null) {
            currentPlayer.pause();
            isPlaying = false;
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
