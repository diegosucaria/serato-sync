import filesystem.MusicLibrary;
import itch.ItchLibrary;
import itch.exception.ItchLibraryException;
import log.Log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Roman Alekseenkov
 */
public class Main {

    private static final String CONFIG_FILE = "itch-sync.properties";

    public static void main(String[] args) {
        Properties config = new Properties();
        try {
            config.load(new FileReader(CONFIG_FILE));
        } catch (IOException e) {
            Log.error("Unable to load config file '" + CONFIG_FILE + "'");
            fatalError();
        }

        String musicLibraryPath = getConfigOption(config, "music.library.filesystem");
        String itchLibraryPath = getConfigOption(config, "music.library.itch");
        boolean guiMode = getConfigGUIOption(config, "mode");
        Log.setMode(guiMode);

        Log.info("Scanning music library " + musicLibraryPath + "...");
        MusicLibrary fsLibrary = MusicLibrary.readFrom(musicLibraryPath);
        if (fsLibrary.getTotalNumberOfTracks() <= 0) {
            Log.error("Unable to find any songs in your music library directory.");
            Log.error("Are you sure you specified the right path in the config file?");
            fatalError();
        } else {
            Log.info("Found " + fsLibrary.getTotalNumberOfTracks() + " tracks in " + fsLibrary.getTotalNumberOfDirectories() + " directories");
        }

        Log.info("Writing files into ITCH library " + itchLibraryPath + "...");
        if (!new File(itchLibraryPath).isDirectory()) {
            Log.error("Unable to detect your ITCH library. It doesn't exist.");
            Log.error("Are you sure you specified the right path in the config file?");
            fatalError();
        }

        ItchLibrary itchLibrary = ItchLibrary.createFrom(fsLibrary);
        try {
            itchLibrary.writeTo(itchLibraryPath);
        } catch (ItchLibraryException e) {
            Log.error("Error occured!");
            Log.error(e);
            fatalError();
        }
        Log.info("Wrote " + itchLibrary.getTotalNumberOfCrates() + " crates and " + itchLibrary.getTotalNumberOfSubCrates() + " subcrates");
        Log.info("Enjoy!");

        success();
    }

    private static void success() {
        Log.success();
        System.exit(0);
    }

    private static void fatalError() {
        Log.fatalError();
        System.exit(-1);
    }

    private static boolean getConfigGUIOption(Properties config, String name) {
        String result = config.getProperty(name);
        return !"cmd".equals(result);
    }

    private static String getConfigOption(Properties config, String name) {
        String result = config.getProperty(name);
        if (result == null || result.trim().length() <= 0) {
            Log.error("The required config option '" + name + "' is not specified");
            System.exit(-1);
        }
        return result;
    }

}
