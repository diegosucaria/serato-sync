package itch;

import filesystem.FileDirectoryUtils;
import filesystem.MusicLibrary;
import itch.exception.ItchLibraryException;

import java.io.File;
import java.util.*;

/**
 * @author Roman Alekseenkov
 */
@SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
public class ItchLibrary {

    private Map<ItchCrate, String> crateFileName = new HashMap<ItchCrate, String>();

    private ItchCrate root;
    private List<ItchCrate> crates = new ArrayList<ItchCrate>();
    private List<ItchCrate> subCrates = new ArrayList<ItchCrate>();

    public static ItchLibrary createFrom(MusicLibrary fsLibrary) {
        // create serato library
        ItchLibrary result = new ItchLibrary();

        // populate serato library it with the tracks from real library
        //
        // note: behavior of serato is slightly different on windows and mac os platforms
        //       when includeSubcrateTracks is set to false, it allows serato on both platforms
        //       to control crates behavior using "include subcrate tracks" option from the "library" menu
        //       without forcing one way or another
        result.buildLibrary(fsLibrary, 0, "", false);

        return result;
    }

    private SortedSet<String> buildLibrary(MusicLibrary fsLibrary, int level, String crateName, boolean includeSubcrateTracks) {
        // create the list of all tracks in this library
        SortedSet<String> all = new TreeSet<String>();

        // add tracks from the current directory
        all.addAll(fsLibrary.getTracks());

        // build everything for every sub-directory
        for (MusicLibrary child : fsLibrary.getChildren()) {
            String crateNameNext = crateName.length() > 0 ? crateName + "%%" + child.getDirectory() : child.getDirectory();
            SortedSet<String> children = buildLibrary(child, level + 1, crateNameNext, includeSubcrateTracks);

            // include subcrate tracks, but only if the option is specified
            if (includeSubcrateTracks) {
                all.addAll(children);
            }
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

    public void writeTo(String itchLibraryPath, boolean clearLibraryBeforeSync) throws ItchLibraryException {

        // let's see if we have to delete existing crates
        if (clearLibraryBeforeSync) {
            // clean legacy 'Crates' directory
            FileDirectoryUtils.deleteAllFilesInDirectory(itchLibraryPath + "/Crates");

            // clean 'Subcrates' directory
            FileDirectoryUtils.deleteAllFilesInDirectory(itchLibraryPath + "/Subcrates");

            // delete 'All' view
            FileDirectoryUtils.deleteFile(itchLibraryPath + "/database V2");
        }

        // write all parent crates
        // they used to be in a separate directory called 'Crates', but now they are also in 'Subcrates'
        for (ItchCrate crate : crates) {
            try {
                crate.writeTo(new File(itchLibraryPath + "/Subcrates/" + crateFileName.get(crate)));
            } catch (ItchLibraryException e) {
                throw new ItchLibraryException("Error while serializing crate '" + crateFileName.get(crate) + "'", e);
            }
        }

        // write all sub-crates to the directory called 'Subcrates'
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
