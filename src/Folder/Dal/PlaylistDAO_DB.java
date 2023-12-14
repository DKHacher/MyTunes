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
    i hate how this looks, and i wrote it.

    Frederik:
    ive taken a closer look, and i think this is still the best way to do so.
    but if i could, i would make a bulk import of songs rather than a while loop.
    lots of unnecessary calls to the database.
    i know it can be done better, but cannot remember how.
    */
    @Override
    public List<Song> getAllSongsInPlaylist(int id) throws Exception {
        List<Song> allSongs = new ArrayList<>();

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "SELECT * FROM dbo.SongPlaylist WHERE PlaylistID = "+id+" order by Position;";
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
    // Frederik: when we generate a new id for the playlist, it remembers deleted playlists id and will generate a new id for the next playlist
    // example would be to create a playlist, newest one we have is number 9, but 5-8 is deleted and so should not count
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
    @Override
    public void updatePlaylist(Playlist playlist) throws Exception {
        // SQL command
        String sql = "UPDATE dbo.Playlist SET Name = ? WHERE ID = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Bind parameters
            stmt.setString(1, playlist.getName());
            stmt.setInt(2, playlist.getId());

            // Run the specified SQL statement
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new Exception("Could not update song", e);
        }
    }

    @Override
    public void deletePlaylist(Playlist playlist) throws Exception {
        // SQL command
        String sql = "DELETE FROM dbo.Playlist WHERE ID = ?;";
        String sql2 = "DELETE FROM dbo.SongPlaylist WHERE PlaylistID = ?;";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
            // Bind parameters
            stmt.setInt(1, playlist.getId());
            stmt2.setInt(1, playlist.getId());

            // Run the specified SQL statement
            stmt.executeUpdate();
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Could not delete playlist", e);
        }
    }
    @Override// Frederik: what the hell even is this
    public void updatePlaylistSongs(Playlist playlist) throws Exception {
        // SQL command
        String sqlDeletePrevious = "DELETE FROM dbo.SongPlaylist WHERE PlaylistID = ?;";
        String sqlCreateNew = "INSERT INTO dbo.SongPlaylist VALUES ";
        int position = 1;
        for (Song song:playlist.getSongList()) {
            String toAdd ="\n" + "(" + song.getId() + ", " + playlist.getId() + ", " + position + "),";
            sqlCreateNew += toAdd;
            position++;
        }
        sqlCreateNew = sqlCreateNew.substring(0,sqlCreateNew.length() - 1);
        sqlCreateNew += ";";
        try (
                Connection conn = dbConnector.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sqlDeletePrevious);
                PreparedStatement stmt2 = conn.prepareStatement(sqlCreateNew)
        ) {
            // Bind parameters
            stmt.setInt(1, playlist.getId());

            // Run the specified SQL statement
            stmt.executeUpdate();
            stmt2.executeUpdate();
        }
        catch (SQLException e) {
            throw new Exception("Could not update songs associated with playlist", e);
        }

    }
}

