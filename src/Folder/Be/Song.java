package Folder.Be;

import java.io.File;
import java.util.Objects;

public class Song {
    private int id;
    private String title;
    private String artist;
    private String genre;
    private int duration; // in milliseconds
    private String filePath;

    public Song(int id, String title, String artist, String genre, int duration, String filePath) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.duration = duration;
        this.filePath = filePath;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public int getDuration() {
        return duration;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", genre='" + genre + '\'' +
                ", duration=" + duration +
                ", filePath='" + filePath + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return id == song.id && duration == song.duration && Objects.equals(title, song.title) && Objects.equals(artist, song.artist) && Objects.equals(genre, song.genre) && Objects.equals(filePath, song.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, artist, genre, duration, filePath);
    }
}