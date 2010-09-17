package filesystem;

import java.io.File;

/**
 * @author Roman Alekseenkov
 */
public class FileDirectoryUtils {

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    public static void deleteAllFilesInDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        if (directory.isDirectory()) {
            File[] all = directory.listFiles();
            for (File file : all)
                if (file.isFile()) {
                    file.delete();
                }
        }
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    public static void deleteFile(String filePath) {
        new File(filePath).delete();
    }

}
