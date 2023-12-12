package Folder.Gui.util;

import Folder.Gui.controller.IDialogController;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A generic builder class for creating and configuring instances of Dialog<T>.<br>
 * It allows for customization of dialog properties such as title, button types, and content.<br>
 * <br>
 * This class utilizes a controller implementing the IDialogController<T> interface
 * to manage the dialog's behavior and data.
 * @param <T>
 */
public class DialogBuilder<T> {
    private final Region view;
    private final IDialogController<T> controller;
    private String title;
    private final List<ButtonType> buttonTypes = new ArrayList<>();

    /**
     * Initializes a DialogBuilder using the specified controller.<br>
     * Initializes the builder with the view provided by the controller.
     *
     * @param controller the IDialogController<T> that manages the dialog
     */
    public DialogBuilder(IDialogController<T> controller) {
        this.controller = controller;
        this.view = controller.getView();
    }

    /**
     * Sets the title of the dialog.
     *
     * @param title the title to be set for the dialog
     * @return the DialogBuilder instance for chaining
     */
    public DialogBuilder<T> withTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Adds button types to the dialog.
     *
     * @param types the button types to be added to the dialog
     * @return the DialogBuilder instance for chaining
     */
    public DialogBuilder<T> addButtonTypes(ButtonType... types) {
        this.buttonTypes.addAll(Arrays.asList(types));
        return this;
    }

    /**
     * Builds and returns a Dialog<T> instance.<br>
     * Sets up the dialog with the specified title, button types, and content provided by the IDialogController<T>.<br>
     * Initializes the dialog using the controller.
     *
     * @return the configured Dialog<T> instance
     */
    public Dialog<T> build() {
        Dialog<T> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.getDialogPane().setContent(view);

        if (!buttonTypes.isEmpty()) {
            dialog.getDialogPane().getButtonTypes().addAll(buttonTypes);
        }

        controller.initializeDialog(dialog);

        return dialog;
    }
}