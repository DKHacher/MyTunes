package Folder.Be;

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
}