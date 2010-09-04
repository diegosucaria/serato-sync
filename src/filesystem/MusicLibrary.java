package filesystem;

import java.io.File;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * @author Roman Alekseenkov
 */
public class MusicLibrary implements Comparable<MusicLibrary> {

    private static final Pattern[] MUSIC_FILENAME_PATTERNS = {
            Pattern.compile("(.*)\\.mp3", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(.*)\\.wav", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(.*)\\.ogg", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(.*)\\.aif", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(.*)\\.aac", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(.*)\\.alac", Pattern.CASE_INSENSITIVE)
    };

    private String directory;
    private SortedSet<String> tracks = new TreeSet<String>();
    private SortedSet<MusicLibrary> children = new TreeSet<MusicLibrary>();

    public MusicLibrary(String directory) {
        this.directory = directory;
    }

    public String getDirectory() {
        return directory;
    }

    public SortedSet<String> getTracks() {
        return tracks;
    }

    public SortedSet<MusicLibrary> getChildren() {
        return children;
    }

    public int getTotalNumberOfTracks() {
        int result = tracks.size();
        for (MusicLibrary childLibrary : children) {
            result += childLibrary.getTotalNumberOfTracks();
        }
        return result;
    }

    public int getTotalNumberOfDirectories() {
        int result = children.size();
        for (MusicLibrary childLibrary : children) {
            result += childLibrary.getTotalNumberOfDirectories();
        }
        return result;
    }

    public static MusicLibrary readFrom(String musicLibraryPath) {
        MusicLibrary result = new MusicLibrary(".");
        result.collectAll(musicLibraryPath);
        return result;
    }

    private void collectAll(String path) {
        File[] all = new File(path).listFiles();
        if (all == null) {
            all = new File[]{};
        }

        // process songs
        for (File file : all)
            if (file.isFile() && isMusic(file)) {
                tracks.add(file.getAbsolutePath());
            }

        // process sub-directories
        for (File file : all)
            if (file.isDirectory()) {
                String childDirectory = file.getName();
                MusicLibrary child = new MusicLibrary(childDirectory);
                child.collectAll(path + "/" + childDirectory);
                children.add(child);
            }
    }

    private boolean isMusic(File file) {
        boolean result = false;
        for (Pattern p : MUSIC_FILENAME_PATTERNS) {
            result |= p.matcher(file.getName().trim()).matches();
        }
        return result;
    }

    public int compareTo(MusicLibrary that) {
        return this.directory.compareTo(that.directory);
    }

    public String toString() {
        return toString(0);
    }

    private String toString(int level) {
        StringBuffer result = new StringBuffer();

        result.append(indent(level) + directory);
        result.append("\n");

        for (String track : tracks) {
            result.append(indent(level + 1) + track);
            result.append("\n");
        }
        for (MusicLibrary library : children) {
            result.append(library.toString(level + 1));
        }
        return result.toString();
    }

    private String indent(int level) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < 2 * level; i++) {
            result.append(' ');
        }
        return result.toString();
    }

}
