import config.Config;
import filesystem.MusicLibrary;
import itch.ItchLibrary;
import itch.exception.ItchLibraryException;
import log.Log;

import java.io.File;
import java.io.IOException;

/**
 * @author Roman Alekseenkov
 */
public class Main {

    public static void main(String[] args) {

        // Load configuration
        Config config;
        try {
            config = new Config();
        } catch (IOException e) {
            Log.error("Unable to load config file '" + Config.CONFIG_FILE + "'");
            Log.fatalError();
            return;
        }

        // Set tool's operating mode - GUI vs. command line
        Log.setMode(config.isGuiMode());

        // Load music library
        Log.info("Scanning music library " + config.getMusicLibraryPath() + "...");
        MusicLibrary fsLibrary = MusicLibrary.readFrom(config.getMusicLibraryPath());
        if (fsLibrary.getTotalNumberOfTracks() <= 0) {
            Log.error("Unable to find any songs in your music library directory.");
            Log.error("Are you sure you specified the right path in the config file?");
            Log.fatalError();
            return;
        }
        Log.info("Found " + fsLibrary.getTotalNumberOfTracks() + " tracks in " + fsLibrary.getTotalNumberOfDirectories() + " directories");

        // Check for existence of serato library
        Log.info("Writing files into serato library " + config.getItchLibraryPath() + "...");
        if (!new File(config.getItchLibraryPath()).isDirectory()) {
            Log.error("Unable to detect your ITCH library. It doesn't exist.");
            Log.error("Are you sure you specified the right path in the config file?");
            Log.fatalError();
            return;
        }

        // Map music library onto serato library
        ItchLibrary itchLibrary = ItchLibrary.createFrom(fsLibrary);
        try {
            itchLibrary.writeTo(config.getItchLibraryPath(), config.isClearLibraryBeforeSync());
        } catch (ItchLibraryException e) {
            Log.error("Error occured!");
            Log.error(e);
            Log.fatalError();
            return;
        }
        Log.info("Wrote " + itchLibrary.getTotalNumberOfCrates() + " crates and " + itchLibrary.getTotalNumberOfSubCrates() + " subcrates");
        Log.info("Enjoy!");

        // Report success and exit
        Log.success();
    }

}
