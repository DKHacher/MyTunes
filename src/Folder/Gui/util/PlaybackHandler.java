package Folder.Gui.util;

import Folder.Be.Song;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class PlaybackHandler {
    private MediaPlayer currentPlayer;
    private Song currentSong;
    private boolean isPlaying;
    private boolean isMuted;

    private DoubleProperty volume = new SimpleDoubleProperty(50.0);
    private StringProperty currentPlayingSong = new SimpleStringProperty("(None) is playing");
    private Double savedVolume = 50.0;

    public PlaybackHandler() {
        isPlaying = false;
        isMuted = false;

        volume.addListener((obs, ov, nv) -> setVolume(nv.doubleValue()));
    }

    public void play(Song song) {
        if (currentPlayer != null && song.equals(currentSong)) {
            currentPlayer.play();
            isPlaying = true;
            setVolume(volume.get());
            currentPlayingSong.set(song.getTitle() + " by " + song.getArtist() + " is playing");
        } else {
            if (currentPlayer != null) {
                currentPlayer.stop();
                isPlaying = false;
                currentPlayingSong.set("(None) is playing");
            }

            Media media = new Media(song.getFile().toURI().toString());
            currentPlayer = new MediaPlayer(media);
            currentSong = song;
            currentPlayer.play();
            isPlaying = true;
            setVolume(volume.get());
            currentPlayingSong.set(song.getTitle() + " by " + song.getArtist() + " is playing");
        }
    }

    public void pause() {
        if (currentPlayer != null) {
            currentPlayer.pause();
            isPlaying = false;
            currentPlayingSong.set("(None) is playing");
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public DoubleProperty volumeProperty() {
        return volume;
    }

    public double getVolume() {
        return volume.get();
    }

    public void setVolume(double volume) {
        this.volume.set(volume);
        if (currentPlayer != null) {
            currentPlayer.setVolume(volume / 100.0);
            isMuted = volume == 0.0;
        }
    }

    public void mute() {
        if (!isMuted) {
            savedVolume = volume.get();
            setVolume(0.0);
            isMuted = true;
        }
    }

    public void unmute() {
        if (isMuted) {
            setVolume(savedVolume);
            isMuted = false;
        }
    }

    public StringProperty getCurrentPlayingSong() {
        return currentPlayingSong;
    }
}
