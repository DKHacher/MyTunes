package Folder.Dal;

import Folder.Be.Song;

import java.util.ArrayList;
import java.util.List;

public class SongDAO_Mock implements ISongDataAccess {
    private List<Song> songs;

    public SongDAO_Mock() {
        songs = new ArrayList<>();
        songs.add(new Song(1, "Test Data", "MusicDAO_Mock", "Test Data", 0, "")); // Test data with zero duration
        songs.add(new Song(2, "Fly Away", "Michael Jackson", "Pop", 214000, ""));
        songs.add(new Song(3, "Gone Too Soon", "Michael Jackson", "Pop", 241000, ""));
        songs.add(new Song(4, "Fire", "Springsteen", "Rock", 162000, ""));
        songs.add(new Song(5, "Hello", "Adele", "Pop", 391000, ""));
        songs.add(new Song(6, "Levitate Me", "SaveUs", "Techno", 294000, ""));
        songs.add(new Song(7, "Wilder Mind", "Momford", "Rock", 312000, ""));
        songs.add(new Song(8, "Chasing Cars", "Snow Patrol", "Pop", 214000, ""));
        songs.add(new Song(9, "The Thrill", "Wiz Khalifa", "Pop", 236000, ""));
        songs.add(new Song(10, "Hard Time", "Seinabo Sey", "Pop", 271000, ""));
        songs.add(new Song(11, "In the Air", "Phil Collins", "Pop", 323000, ""));
        songs.add(new Song(12, "Cover Me", "Springsteen", "Rock", 248000, ""));
        songs.add(new Song(13, "Under My Thumb", "Rolling Stones", "Rock", 375000, ""));
        songs.add(new Song(14, "Titanium", "Guetta", "Techno", 212000, ""));
    }

    @Override
    public List<Song> getAllSongs() throws Exception {
        return songs;
    }

    @Override
    public List<String> getAllGenres() throws Exception {
        return List.of("Pop", "Rock", "Metal", "Country");
    }

    @Override
    public Song createSong(Song song) throws Exception {
        songs.add(song);
        return song;
    }

    @Override
    public void updateSong(Song song) throws Exception {
        return;
    }

    @Override
    public void deleteSong(Song song) throws Exception {
        return;
    }

    @Override
    public List<Song> filterSongs(String searchText) throws Exception {
        return null;
    }
}
