package Folder.Gui.util;

import Folder.Be.Song;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Optional;

/**
 * Manages a reactive queue of songs to be played in a media player.<br>
 * <br>
 * This class is designed to react to changes in the provided ObservableList of songs.<br>
 * Allows for navigating through the queue, by moving to the next,
 * previous, first, or a specific song, and keeping track of the current song.
 */
public class SongQueue {
    private final ObservableList<Song> queue;
    private int currentIndex;

    /**
     * Initializes a new reactive SongQueue with a list of songs.<br>
     * <br>
     * The songs in the queue are managed in the order they appear in the list.<br>
     * The queue initially starts at the beginning of the list.<br>
     * The SongQueue reacts to changes in the provided list and adjusts itself as necessary.
     *
     * @param songs the ObservableList of songs to initialize the queue. The list should not be null.<br>
     *              Modifications to this list directly affects the SongQueue,
     *              as it uses the list directly for queue management.
     * @throws IllegalArgumentException if the songs list is null.
     */
    public SongQueue(ObservableList<Song> songs) {
        if (songs == null) throw new IllegalArgumentException("Songs list cannot be null");
        this.queue = songs;
        currentIndex = 0;

        queue.addListener((ListChangeListener<Song>) c -> {
            while (c.next()) {
                if (c.wasRemoved()) {
                    handleSongsRemoved(c.getFrom(), c.getRemovedSize());
                }

                if (c.wasAdded()) {
                    handleSongsAdded(c.getFrom(), c.getAddedSize());
                }
            }
        });
    }

    private void handleSongsRemoved(int fromIndex, int removedSize) {
        // We want to adjust currentIndex if a song is removed before or at the currentIndex.

        // We update currentIndex to ensure it remains within valid range, that is:
        // - It should not be less than 0: the (Math.max(0, ...)) part does this.
        // - It should not exceed the size of the queue after removal: the (Math.min(..., queue.size() - 1)) part does this.
        if (currentIndex >= fromIndex) {
            currentIndex = Math.max(0, Math.min(currentIndex - removedSize, queue.size() - 1));
        }
    }

    private void handleSongsAdded(int fromIndex, int addedSize) {
        // If a song is added to the ObservableList at the end it will automatically work.
        // This is to ensure if it is being added but not at the end.
        if (fromIndex <= currentIndex) {
            currentIndex += addedSize;
        }
    }

    /**
     * Retrieves the first song in the queue.
     *
     * @return Optional containing the first song if present, else an empty Optional.
     */
    public Optional<Song> first() {
        if (queue.isEmpty()) return Optional.empty();

        currentIndex = 0;
        return Optional.of(queue.get(0));
    }

    /**
     * Retrieves the next song in the queue.
     *
     * @return Optional containing the next song if present, else an empty Optional.
     */
    public Optional<Song> next() {
        if (currentIndex + 1 < queue.size()) {
            currentIndex++;
            return Optional.of(queue.get(currentIndex));
        }

        return Optional.empty();
    }

    /**
     * Retrieves the previous song in the queue.
     *
     * @return Optional containing the previous song if present, else an empty Optional.
     */
    public Optional<Song> previous() {
        if (currentIndex > 0) {
            currentIndex--;
            return Optional.of(queue.get(currentIndex));
        }

        return Optional.empty();
    }

    /**
     * Selects a specific song in the queue.
     *
     * @param song the song to be selected.
     * @return true if the song is found and selected, false otherwise.
     */
    public boolean select(Song song) {
        int index = queue.indexOf(song);
        if (index != -1) {
            currentIndex = index;
            return true;
        }

        return false;
    }

    /**
     * Retrieves the current song in queue.
     *
     * @return Optional containing the current song if present, else an empty Optional.
     */
    public Optional<Song> current() {
        if (currentIndex >= 0 && currentIndex < queue.size()) {
            return Optional.of(queue.get(currentIndex));
        }

        return Optional.empty();
    }

    /**
     * Returns true if this queue contains no songs, false otherwise.
     *
     * @return true if this queue contains no songs, false otherwise.
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }
}