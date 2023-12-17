package Folder.Gui.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileManagementService {
    public void copyFileToDir(File sourceFile, String destDir) throws IOException {
        File destFile = new File(destDir, sourceFile.getName());
        System.out.println(destFile);

        File dir = new File(destDir);
        if (!dir.exists()) dir.mkdir();

        Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public void deleteFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists() && !file.delete()) {
            throw new IOException("Failed to delete file: " + filePath);
        }
    }
}
