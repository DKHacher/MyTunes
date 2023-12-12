package Folder.Gui.util;

import Folder.Be.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a queue of songs to be played in a media player.<br>
 * <br>
 * Allows for navigating through the list of songs, including selecting a specific song,
 * moving to the next or previous song, and retrieving the current song in the queue.
 */
public class SongQueue {
    private final List<Song> queue;
    private int currentIndex;

    /**
     * Constructs a SongQueue with a provided list of songs.<br>
     * <br>
     * The songs in the queue are managed in the order they appear in the list.<br>
     * The queue initially starts at the beginning of the list.
     *
     * @param songs the list of songs to initialize the queue. The list should not be null.
     * @throws IllegalArgumentException if the songs list is null.
     */
    public SongQueue(List<Song> songs) {
        if (songs == null) throw new IllegalArgumentException("Songs list cannot be null");
        this.queue = new ArrayList<>(songs);
        currentIndex = 0;
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