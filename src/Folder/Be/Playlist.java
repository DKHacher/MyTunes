package Folder.Be;

import Folder.Gui.util.TimeStringConverter;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private int id;
    private String name;
    private List<Song> songList;
    private int amountOfSongs;
    private int playlistDuration; //in milliseconds, just like in songs



    public Playlist(int id, String name, List<Song> allSongsInPlaylist) {
        this.id = id;
        this.name = name;
        this.songList = allSongsInPlaylist;
        this.amountOfSongs = allSongsInPlaylist.size();
        this.playlistDuration = getTimeFromPlaylist(allSongsInPlaylist);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public  List<Song> getSongList() {
        return songList;
    }
    public int getAmountOfSongs() {
        return amountOfSongs;
    }
    public int getPlaylistDuration() { return playlistDuration;}
    public void setName(String name) {
        this.name = name;
    }

    private int getTimeFromPlaylist(List<Song> allSongsInPlaylist){
        int time = 0;
        for (Song song:allSongsInPlaylist) {
            time += song.getDuration();
        }
        return time;
    }
    public List<String> getSongsInPlaylistAsString(){
        List<String> StringSongList = new ArrayList<>();
        int i = 1;
    for (Song song: songList) {
        String duration = new TimeStringConverter().toString(song.getDuration());
        StringSongList.add(i + ", " + song.getTitle() + ", " + duration);
        i++;
    }
    return StringSongList;
    }
    

}

