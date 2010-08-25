import filesystem.MusicLibrary;
import itch.ItchLibrary;
import itch.exception.ItchLibraryException;

public class Main {

    private static final String ITCH_LIBRARY_PATH = System.getProperty("user.home") + "/Music/_Serato_";
    private static final String MUSIC_LIBRARY_PATH = System.getProperty("user.home") + "/Music/iTunes/iTunes Music/Music";

    public static void main(String[] args) {
        Log.info("Scanning music library " + MUSIC_LIBRARY_PATH + "...");
        MusicLibrary fsLibrary = MusicLibrary.readFrom(MUSIC_LIBRARY_PATH);
        Log.info("Found " + fsLibrary.getTotalNumberOfTracks() + " tracks in " + fsLibrary.getTotalNumberOfDirectories() + " directories");

        Log.info("Writing files into ITCH library " + ITCH_LIBRARY_PATH + "...");
        ItchLibrary itchLibrary = ItchLibrary.createFrom(fsLibrary);
        try {
            itchLibrary.writeTo(ITCH_LIBRARY_PATH);
        } catch (ItchLibraryException e) {
            throw new IllegalStateException(e);
        }
        Log.info("Wrote " + itchLibrary.getTotalNumberOfCrates() + " crates and " + itchLibrary.getTotalNumberOfSubCrates() + " subcrates");
    }

}
