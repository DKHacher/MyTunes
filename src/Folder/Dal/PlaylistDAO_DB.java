package Folder.Dal;

import Folder.Be.Playlist;
import Folder.Be.Song;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDAO_DB implements IPlaylistDataAccess {
    private final DatabaseConnector dbConnector;

    public PlaylistDAO_DB() throws IOException  {
        dbConnector = new DatabaseConnector();
    }

    @Override
    public List<Playlist> getAllPlaylists() throws Exception {
        List<Playlist> allPlaylists = new ArrayList<>();

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "SELECT * FROM dbo.Playlist;";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("ID");
                String title = rs.getString("Name");

                Playlist playlist = new Playlist(id, title, getAllSongsInPlaylist(id));
                allPlaylists.add(playlist);
            }

            return allPlaylists;
        } catch (SQLException e) {
            throw new Exception("Error connecting to the database");
        }
    }
    /*
    Frederik:
    attempting to get all songs associated with a playlist
    this code is cursed, I want someone else to look at it and
    tell me if what im doing could be done better
    i hate how this looks, and i wrote it -still Frederik
    */
    @Override
    public List<Song> getAllSongsInPlaylist(int id) throws Exception {
        List<Song> allSongs = new ArrayList<>();

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "SELECT * FROM dbo.SongPlaylist WHERE PlaylistID = "+id+";";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int Sid = rs.getInt("SongID");
                String songToImport = "SELECT * FROM dbo.Song WHERE ID = "+Sid+";";
                Statement stmt2 = conn.createStatement();
                ResultSet rs2 = stmt2.executeQuery(songToImport);
                while (rs2.next()){
                    int SOid = rs2.getInt("ID");
                    String title = rs2.getString("Title");
                    String artist = rs2.getString("Artist");
                    String genre = rs2.getString("Genre");
                    int duration = rs2.getInt("Duration");
                    String filePath = rs2.getString("FilePath");

                    Song song = new Song(SOid, title, artist, genre, duration, filePath);
                    allSongs.add(song);
                }
            }
            return allSongs;
        } catch (SQLException e) {
            throw new Exception("Error connecting to the database");
        }
    }


    @Override
    public Playlist createPlaylist(Playlist playlist) throws Exception {
        String sql = "INSERT INTO dbo.Playlist (Name) VALUES (?);";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Bind parameters
            stmt.setString(1, playlist.getName());

            // Run SQL statement
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            int id = 0;

            if (rs.next()) {
                id = rs.getInt(1);
            }

            return new Playlist(id, playlist.getName(), getAllSongsInPlaylist(id));
        } catch (SQLException e) {
            throw new Exception("Could not add Playlist to DB", e);
        }
    }
}
