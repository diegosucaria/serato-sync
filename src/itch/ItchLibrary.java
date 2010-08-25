package itch;

import filesystem.MusicLibrary;
import itch.exception.ItchLibraryException;

import java.io.File;
import java.util.*;

public class ItchLibrary {

    private Map<ItchCrate, String> crateFileName = new HashMap<ItchCrate, String>();

    private ItchCrate root;
    private List<ItchCrate> crates = new ArrayList<ItchCrate>();
    private List<ItchCrate> subCrates = new ArrayList<ItchCrate>();

    public static ItchLibrary createFrom(MusicLibrary fsLibrary) {
        ItchLibrary result = new ItchLibrary();
        result.buildLibrary(fsLibrary, 0, "");
        return result;
    }

    private SortedSet<String> buildLibrary(MusicLibrary fsLibrary, int level, String crateName) {
        SortedSet<String> all = new TreeSet<String>();
        all.addAll(fsLibrary.getTracks());

        for (MusicLibrary child : fsLibrary.getChildren()) {
            String crateNameNext = !crateName.isEmpty() ? crateName + "%%" + child.getDirectory() : child.getDirectory();
            SortedSet<String> children = buildLibrary(child, level + 1, crateNameNext);
            all.addAll(children);
        }

        ItchCrate crate = new ItchCrate();
        crate.addTracks(all);
        if (level == 0) {
            // this is a global crate that corresponds to everything, we don't really need it but will store... just in case
            root = crate;
        } else if (level == 1) {
            // this is a first-level crate
            crates.add(crate);
        } else {
            // this is a sub-crate
            subCrates.add(crate);
        }
        crateFileName.put(crate, crateName + ".crate");

        return all;
    }

    public void writeTo(String itchLibraryPath) throws ItchLibraryException {
        for (ItchCrate crate : crates) {
            try {
                crate.writeTo(new File(itchLibraryPath + "/Crates/" + crateFileName.get(crate)));
            } catch (ItchLibraryException e) {
                throw new ItchLibraryException("Error while serializing crate '" + crateFileName.get(crate) + "'", e);
            }
        }
        for (ItchCrate crate : subCrates) {
            try {
                crate.writeTo(new File(itchLibraryPath + "/Subcrates/" + crateFileName.get(crate)));
            } catch (ItchLibraryException e) {
                throw new ItchLibraryException("Error while serializing subcrate '" + crateFileName.get(crate) + "'", e);
            }
        }

    }

    public int getTotalNumberOfCrates() {
        return crates.size();
    }

    public int getTotalNumberOfSubCrates() {
        return subCrates.size();
    }

}
