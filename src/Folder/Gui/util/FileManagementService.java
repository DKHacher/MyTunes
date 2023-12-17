package Folder.Gui.util;

import Folder.Config.AppConfig;
import Folder.Config.ConfigProperty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileManagementService {
    public void copyFileToDir(File sourceFile, String destDir) throws IOException {
        File destFile = new File(destDir, sourceFile.getName());

        File dir = new File(destDir);
        if (!dir.exists()) dir.mkdir();

        Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
