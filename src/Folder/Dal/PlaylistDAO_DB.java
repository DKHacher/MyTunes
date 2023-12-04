package Folder.Dal;

import Folder.Be.Playlist;

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

                Playlist playlist = new Playlist(id, title);
                allPlaylists.add(playlist);
            }

            return allPlaylists;
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

            return new Playlist(id, playlist.getName());
        } catch (SQLException e) {
            throw new Exception("Could not add Playlist to DB", e);
        }
    }
}
