package Folder.Gui.model;

import Folder.Be.Playlist;
import Folder.Be.Song;
import Folder.Gui.util.TimeStringConverter;
import javafx.beans.Observable;
import javafx.beans.property.*;

public class PlaylistDialogModel {
    private final IntegerProperty id = new SimpleIntegerProperty(-1);
    private final StringProperty name = new SimpleStringProperty("");


    public PlaylistDialogModel() {
        reset();
    }

    public void setProperties(Playlist playlist) {
        if (playlist != null) {
            setId(playlist.getId());
            setName(playlist.getName());

        }
    }

    public Observable[] getBindableProperties() {
        return new Observable[] {
                idProperty(),
                nameProperty(),
        };
    }

    public void reset() {
        setId(-1);
        setName("");
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

}
