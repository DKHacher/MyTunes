package Folder.Gui.model;

import Folder.Be.Song;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

public class PlaybackModel {
    private SimpleBooleanProperty isPlaying = new SimpleBooleanProperty(false);
    private SimpleDoubleProperty currenTime = new SimpleDoubleProperty(0.0);
    private SimpleDoubleProperty totalDuration = new SimpleDoubleProperty(0.0);
    private SimpleDoubleProperty currentPosition = new SimpleDoubleProperty(0.0);
    private SimpleDoubleProperty volume = new SimpleDoubleProperty(0.0);
    private SimpleBooleanProperty isMuted = new SimpleBooleanProperty(false);
    private SimpleObjectProperty<Song> currentSong = new SimpleObjectProperty<>();

    public boolean getIsPlaying() {
        return isPlaying.get();
    }

    public SimpleBooleanProperty isPlayingProperty() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying.set(isPlaying);
    }

    public double getCurrenTime() {
        return currenTime.get();
    }

    public SimpleDoubleProperty currenTimeProperty() {
        return currenTime;
    }

    public void setCurrenTime(double currenTime) {
        this.currenTime.set(currenTime);
    }

    public double getTotalDuration() {
        return totalDuration.get();
    }

    public SimpleDoubleProperty totalDurationProperty() {
        return totalDuration;
    }

    public void setTotalDuration(double totalDuration) {
        this.totalDuration.set(totalDuration);
    }

    public double getCurrentPosition() {
        return currentPosition.get();
    }

    public SimpleDoubleProperty currentPositionProperty() {
        return currentPosition;
    }

    public void setCurrentPosition(double currentPosition) {
        this.currentPosition.set(currentPosition);
    }

    public double getVolume() {
        return volume.get();
    }

    public SimpleDoubleProperty volumeProperty() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume.set(volume);
    }


    public boolean getIsMuted() {
        return isMuted.get();
    }

    public SimpleBooleanProperty isMutedProperty() {
        return isMuted;
    }

    public void setIsMuted(boolean isMuted) {
        this.isMuted.set(isMuted);
    }

    public Song getCurrentSong() {
        return currentSong.get();
    }

    public ObjectProperty<Song> currentSongProperty() {
        return currentSong;
    }

    public void setCurrentSong(Song currentSong) {
        this.currentSong.set(currentSong);
    }

}
