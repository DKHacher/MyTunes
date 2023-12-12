package Folder.Gui.util;

import Folder.Be.Song;
import Folder.Bll.SongQueue;
import Folder.Common.SongPlaybackException;
import Folder.Gui.model.PlaybackModel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles media playback functionality, including playing, pausing,
 * seeking, and managing a queue of songs.
 */
public class PlaybackHandler {
    private static final double START_VOLUME = 50.0;

    private final PlaybackModel model;

    private MediaPlayer currentPlayer;
    private double savedVolume;
    private SongQueue songQueue;

    /**
     * Initializes a new PlaybackHandler with a specified model.
     *
     * @param model the model containing playback state and properties.
     */
    public PlaybackHandler(PlaybackModel model) {
        this.model = model;
        model.setVolume(START_VOLUME);
        songQueue = new SongQueue(new ArrayList<>());
    }

    /**
     * Plays the specified song.<br>
     * <br>
     * If the same song is currently playing, playback will continue without restarting the song.<br>
     * For example, if song 'a' is playing, then playing song 'a' again won't have any effect.<br>
     * <br>
     * If a queue has been set it will automatically play the next song in queue when finished with this.<br>
     * If a queue has not been set it will stop playing when finishes with this.
     *
     * @param song the song to be played. Requires that the song is not null and is a valid, playable song.
     *             That is, the song must have a valid file path and format (.mp3 or .wav).
     * @throws SongPlaybackException if the song cannot be played due to e.g., being corrupted or having an invalid file path or format (.mp3 or .wav).
     * @throws IllegalArgumentException if the song is null.
     */
    public void play(Song song) throws SongPlaybackException {
        if (song == null) throw new IllegalArgumentException("Song cannot be null");

        //String filePath = AppConfig.INSTANCE.getProperty("fileDirectory") + song.getFilePath();
        String filePath = song.getFilePath(); // For testing, need to change Song table in DB to use fileName only and not full filePath

        File file = new File(filePath);
        if (!isValidSong(song)) {
            throw new SongPlaybackException(
                    "Error playing the song: " + song.getFilePath() + "\n" +
                    "Invalid file path or format"
            );
        }

        songQueue.select(song);

        if (song.equals(model.getCurrentSong()) && currentPlayer != null) {
            currentPlayer.play();
            model.setIsPlaying(true);
        } else {
            if (currentPlayer != null && model.getCurrentSong() != null) {
                stopSong();
            }

            try {
                initializeNewPlayer(file);
                currentPlayer.play();
                model.setCurrentSong(song);
                model.setIsPlaying(true);

                currentPlayer.setOnEndOfMedia(() -> {
                    if (songQueue.isEmpty()) {
                        stopSong();
                        model.setCurrentSong(null);
                    } else {
                        nextSong();
                    }
                });
            } catch (MediaException e) {
                throw new SongPlaybackException(
                        "Error playing the song: " + filePath + "\n" +
                                "Check if the music file isn't corrupted."
                );
            }
        }
    }

    /**
     * Pauses the playback.<br>
     * <br>
     * If a song is playing, it is paused without changing the current position in the song.
     */
    public void pause() {
        if (currentPlayer != null) {
            currentPlayer.pause();
            model.setIsPlaying(false);
        }
    }

    /**
     * Seeks to a specified position in the current song.
     *
     * @param position the position to seek to, must be within the range of 0.0 and 100.0.
     * @throws IllegalArgumentException if position is out of the valid range.
     */
    public void seek(double position) {
        if (position < 0.0 || position > 100.0) {
            throw new IllegalArgumentException("Position must be between 0.0 and 100.0");
        }

        if (currentPlayer != null && model.getCurrentSong() != null) {
            double totalDuration = currentPlayer.getTotalDuration().toSeconds();
            double seekPosition = (position / 100.0) * totalDuration;
            currentPlayer.seek(Duration.seconds(seekPosition));
        }
    }

    /**
     * Sets the playback volume.
     *
     * @param volume the volume level to be set, must be within the range of 0.0 and 100.0.
     * @throws IllegalArgumentException if volume is out of the valid range.
     */
    public void setVolume(double volume) {
        if (volume < 0.0 || volume > 100.0) {
            throw new IllegalArgumentException("Volume must be between 0.0 and 100.0");
        }

        model.setVolume(volume);
        if (currentPlayer != null) {
            currentPlayer.setVolume(volume / 100.0);
            model.setIsMuted(volume == 0.0);
        }
    }

    /**
     * Mutes the playback.
     */
    public void mute() {
        if (!model.getIsMuted()) {
            savedVolume = model.getVolume();
            setVolume(0.0);
            model.setIsMuted(true);
        }
    }

    /**
     * Unmutes the playback.<br>
     * Restores the playback to the previous volume level.
     */
    public void unmute() {
        if (model.getIsMuted()) {
            setVolume(savedVolume);
            model.setIsMuted(false);
        }
    }

    /**
     * Sets the queue of songs to be played or replaces the current song queue with the specified queue.<br>
     * This method does not modify the provided queue list.
     *
     * @param queue The list of songs to be set as the queue.
     * @throws IllegalArgumentException If queue is null, contains null elements.
     */
    public void setQueue(List<Song> queue) {
        if (queue == null) throw new IllegalArgumentException("Queue cannot be null");

        songQueue = new SongQueue(queue);
    }

    /**
     * Begins playback from current queue.<br>
     * <br>
     * If the queue is empty or has not been set, no action is taken.<br>
     * If the queue is not empty, starts playback from the first song in the queue.<br>
     * Subsequent calls to this method will resume playback from the current position in the queue.
     */
    public void playQueue() {
        songQueue.current().ifPresent(this::playSongHandlingException);
    }

    /**
     * Plays the next song in the queue if there is one.
     */
    public void nextSong() {
        songQueue.next().ifPresent(this::playSongHandlingException);
    }

    /**
     * Plays the previous song in the queue if there is one.
     */
    public void previousSong() {
        songQueue.previous().ifPresent(this::playSongHandlingException);
    }

    private boolean isValidSong(Song song) {
        File file = new File(song.getFilePath());
        return file.exists() && isPlayableFormat(file);
    }

    private boolean isPlayableFormat(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".mp3") || fileName.endsWith(".wav");
    }

    private void initializeNewPlayer(File file) throws MediaException {
        Media media = new Media(file.toURI().toString());
        currentPlayer = new MediaPlayer(media);
        currentPlayer.setVolume(model.getVolume() / 100.0);

        currentPlayer.totalDurationProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                model.setTotalDuration(nv.toSeconds());
            }
        });

        currentPlayer.currentTimeProperty().addListener((obs, ov, nv) -> {
            if (nv != null && currentPlayer.getTotalDuration() != null) {
                double currentPosition = nv.toSeconds();
                double totalDuration = currentPlayer.getTotalDuration().toSeconds();
                model.setCurrentPosition((currentPosition / totalDuration) * 100.0);
                model.setCurrenTime(nv.toSeconds());
            }
        });
    }

    private void stopSong() {
        currentPlayer.stop();
        currentPlayer.dispose();
        model.setIsPlaying(false);
    }

    private void playSongHandlingException(Song song) {
        try {
            play(song);
        } catch (SongPlaybackException e) {
            throw new RuntimeException("Error playing the song: " + e.getMessage(), e);
        }
    }
}
