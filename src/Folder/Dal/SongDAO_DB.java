package Folder.Dal;

import Folder.Be.Song;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SongDAO_DB implements ISongDataAccess {
    private final DatabaseConnector dbConnector;

    public SongDAO_DB() throws IOException  {
        dbConnector = new DatabaseConnector();
    }

    @Override
    public List<Song> getAllSongs() throws Exception {
        List<Song> allSongs = new ArrayList<>();

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "SELECT * FROM dbo.Song;";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("ID");
                String title = rs.getString("Title");
                String artist = rs.getString("Artist");
                String genre = rs.getString("Genre");
                int duration = rs.getInt("Duration");
                String filePath = rs.getString("FilePath");

                Song song = new Song(id, title, artist, genre, duration, filePath);
                allSongs.add(song);
            }

            return allSongs;
        } catch (SQLException e) {
            throw new Exception("Error connecting to the database");
        }
    }

    @Override
    public List<String> getAllGenres() throws Exception {
        List<String> allGenres = new ArrayList<>();

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "SELECT DISTINCT Genre FROM dbo.Song ORDER BY Genre ASC;";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                allGenres.add(rs.getString("Genre"));
            }

            return allGenres;
        } catch (SQLException e) {
            throw new Exception("Error connecting to the database");
        }
    }

    @Override
    public Song createSong(Song song) throws Exception {
        String sql = "INSERT INTO dbo.Song (Title, Artist, Genre, Duration, FilePath) VALUES (?,?,?,?,?);";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Bind parameters
            stmt.setString(1, song.getTitle());
            stmt.setString(2, song.getArtist());
            stmt.setString(3, song.getGenre());
            stmt.setInt(4, song.getDuration());
            stmt.setString(5, song.getFilePath());

            // Run SQL statement
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            int id = 0;

            if (rs.next()) {
                id = rs.getInt(1);
            }

            return new Song(id, song.getTitle(), song.getArtist(), song.getGenre(), song.getDuration(), song.getFilePath());
        } catch (SQLException e) {
            throw new Exception("Could not add song to DB", e);
        }
    }

    @Override
    public void updateSong(Song song) throws Exception {
        // SQL command
        String sql = "UPDATE dbo.Song SET Title = ?, Artist = ?, Genre = ?, Duration = ?, FilePath = ? WHERE ID = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Bind parameters
            stmt.setString(1, song.getTitle());
            stmt.setString(2, song.getArtist());
            stmt.setString(3, song.getGenre());
            stmt.setInt(4, song.getDuration());  // Assuming duration is stored as an integer (milliseconds)
            stmt.setString(5, song.getFilePath());
            stmt.setInt(6, song.getId());

            // Run the specified SQL statement
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new Exception("Could not update song", e);
        }
    }
}
