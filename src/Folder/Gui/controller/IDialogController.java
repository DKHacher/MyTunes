package Folder.Gui.controller;

import javafx.scene.control.Dialog;
import javafx.scene.layout.Region;

/**
 * Interface for a dialog controller.
 *
 * @param <T> the type of the object that this controller will use to initialize and manage the dialog.
 */
public interface IDialogController<T> {

    /**
     * Returns the Region that represents the view for the dialog.<br>
     * This method does not modify the state of the controller.
     *
     * @return the Region object that represents the user interface of the dialog.
     */
    Region getView();

    /**
     * Initializes the given dialog with necessary properties and event handlers.<br>
     * This method is expected to set up the dialog's behavior and interactions with the user.<br>
     * The method modifies the passed Dialog<T> object by setting its properties and event handlers,
     * facilitating the intended functionality.
     *
     * @param dialog the Dialog<T> instance to be initialized
     */
    void initializeDialog(Dialog<T> dialog);
}
