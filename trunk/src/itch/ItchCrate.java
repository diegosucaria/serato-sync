package itch;

import itch.exception.ItchLibraryException;
import itch.io.ItchInputStream;
import itch.io.ItchOutputStream;

import java.io.*;
import java.util.*;

/**
 * @author Roman Alekseenkov
 */
public class ItchCrate {

    private static final String DEFAULT_VERSION = "81.0";
    private static final String DEFAULT_SORTING = "song";
    private static final long DEFAULT_SORTING_REV = 1 << 8;
    private static final String[] DEFAULT_COLUMNS = {"song", "artist", "album", "length"};

    private String version;

    private String sorting;
    private long sortingRev = Integer.MIN_VALUE;

    private List<String> columns = new ArrayList<String>();
    private List<String> tracks = new ArrayList<String>();

    public ItchCrate() {
    }

    public void addTrack(String nameAsString) {
        tracks.add(nameAsString);
    }

    public void addTracks(Collection<String> names) {
        tracks.addAll(names);
    }

    public void setVersion(String version) {
        if (version.length() != 4) {
            throw new IllegalStateException("Version should be 4 characters long. E.g. default version is '" + DEFAULT_VERSION + "'");
        }
        this.version = version;
    }

    public String getVersion() {
        return version != null ? version : DEFAULT_VERSION;
    }

    public String getSorting() {
        return sorting != null ? sorting : DEFAULT_SORTING;
    }

    public void setSorting(String sorting) {
        this.sorting = sorting;
    }

    public long getSortingRev() {
        return sortingRev != Integer.MIN_VALUE ? sortingRev : DEFAULT_SORTING_REV;
    }

    public void setSortingRev(long sortingRev) {
        this.sortingRev = sortingRev;
    }

    public void addColumn(String nameAsString) {
        columns.add(nameAsString);
    }

    public Collection<String> getColumns() {
        return !columns.isEmpty() ?
                Collections.unmodifiableCollection(columns) :
                Arrays.asList(DEFAULT_COLUMNS);
    }

    public Collection<String> getTracks() {
        return Collections.unmodifiableCollection(tracks);
    }

    /**
     * Reads crate from file
     *
     * @param inFile file to read the crate from
     * @return Crate object
     * @throws ItchLibraryException if something went wrong during crate reading
     */
    public static ItchCrate readFrom(File inFile) throws ItchLibraryException {
        ItchCrate result = new ItchCrate();

        ItchInputStream in;

        try {
            in = new ItchInputStream(new FileInputStream(inFile));
        } catch (FileNotFoundException e) {
            throw new ItchLibraryException(e);
        }

        try {
            // header
            {
                {
                    // version
                    in.skipExactString("vrsn");
                    in.skipExactByte((byte) 0);
                    in.skipExactByte((byte) 0);
                    result.setVersion(in.readStringUTF16(8));
                    in.skipExactStringUTF16("/Serato ScratchLive Crate");
                }

                {
                    // handle different blocks
                    for (; ;) {
                        String type = in.readString(4);
                        if ("otrk".equals(type)) {
                            // time to start reading tracks
                            break;
                        }

                        if ("ovct".equals(type)) {

                            // handle 'ovct' - columns
                            int ovctValue = in.readIntegerValue();

                            in.skipExactString("tvcn");
                            int tvcnValue = in.readIntegerValue();

                            String column = in.readStringUTF16(tvcnValue);
                            result.addColumn(column);

                            in.skipExactString("tvcw");
                            int tvcwValue = in.readIntegerValue();
                            in.skipExactByte((byte) 0);
                            in.skipExactByte((byte) '0');

                            // ovct/tvcn assertion
                            if (ovctValue - tvcnValue != 18) {
                                throw new ItchLibraryException("Expected (ovct - tvcn) == 18, but found " + (ovctValue - tvcnValue) + " (ovct = " + ovctValue + ", tvcn = " + tvcnValue + ")");
                            }

                            // tvcw assertion
                            if (tvcwValue != 2) {
                                throw new ItchLibraryException("Expected tvcw == 2, but found " + tvcwValue);
                            }

                        } else if ("osrt".equals(type)) {

                            // handle 'osrt' - sorting
                            int osrtValue = in.readIntegerValue();

                            in.skipExactString("tvcn");
                            int tvcnValue = in.readIntegerValue();

                            String sorting = in.readStringUTF16(tvcnValue);
                            result.setSorting(sorting);

                            in.skipExactString("brev");
                            long sortingRev = in.readLongValue(5);
                            result.setSortingRev(sortingRev);

                            // osrt/tvcn assertion
                            if (osrtValue - tvcnValue != 17) {
                                throw new ItchLibraryException("Expected (osrt - tvcn) == 17, but found " + (osrtValue - tvcnValue) + " (osrt = " + osrtValue + ", tvcn = " + tvcnValue + ")");
                            }
                        }

                    }
                }


                // read all tracks
                {
                    boolean firstTrack = true;
                    for (; ;) {

                        if (!firstTrack) {
                            // otrk as string
                            boolean eof = in.skipExactString("otrk");
                            if (eof) {
                                break;
                            }
                        }
                        firstTrack = false;

                        // not sure what these 4 bytes are about, but they seem to be nameLength + 8
                        int nameLengthPlus8 = in.readIntegerValue();

                        // ptrk as string
                        in.skipExactString("ptrk");

                        // likely all these 4 bytes is a length of the track name
                        int nameLength = in.readIntegerValue();

                        // otrk/ptrk assertion
                        if (nameLengthPlus8 - nameLength != 8) {
                            throw new ItchLibraryException("Expected (otrk - ptrk) == 8, but found " + (nameLengthPlus8 - nameLength) + " (otrk = " + nameLengthPlus8 + ", ptrk = " + nameLength + ")");
                        }

                        // track name
                        String nameAsString = in.readStringUTF16(nameLength);
                        result.addTrack(nameAsString);
                    }
                }
            }


        } finally {
            try {
                in.close();
            } catch (IOException e) {
                // do nothing
            }
        }

        return result;
    }

    /**
     * Writes crate into stream
     *
     * @param outStream print stream to write the result to
     * @throws ItchLibraryException if something went wrong during crate writing
     */
    public void writeTo(OutputStream outStream) throws ItchLibraryException {
        ItchOutputStream out = new ItchOutputStream(outStream);

        try {
            // header
            {
                {
                    // version
                    out.writeBytes("vrsn");
                    out.write((byte) 0);
                    out.write((byte) 0);
                    out.writeUTF16(getVersion());
                    out.writeUTF16("/Serato ScratchLive Crate");
                }

                {
                    // sorting
                    out.writeBytes("osrt");
                    out.writeInt(getSorting().length() * 2 + 17);

                    out.writeBytes("tvcn");
                    out.writeInt(getSorting().length() * 2);

                    out.writeUTF16(getSorting());

                    out.writeBytes("brev");
                    out.writeLong(getSortingRev(), 5);
                }

                {
                    // display columns
                    for (String column : getColumns()) {
                        out.writeBytes("ovct");
                        out.writeInt(column.length() * 2 + 18);

                        out.writeBytes("tvcn");
                        out.writeInt(column.length() * 2);

                        out.writeUTF16(column);

                        out.writeBytes("tvcw");
                        out.writeInt(2);

                        out.write(0);
                        out.write('0');
                    }
                }


                // read all tracks
                {
                    for (String trackRaw : getTracks()) {

                        String track = getUniformTrackName(trackRaw);

                        out.writeBytes("otrk");

                        // not sure what these 4 bytes are about, but they seem to be nameLength + 8
                        out.writeInt(track.length() * 2 + 8);

                        // ptrk as string
                        out.writeBytes("ptrk");

                        // likely all these 4 bytes is a length of the track name
                        out.writeInt(track.length() * 2);

                        // track name
                        out.writeUTF16(track);
                    }
                }
            }


        } catch (IOException e) {
            throw new ItchLibraryException(e);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                // do nothing
            }
        }

    }

    /**
     * No matter whether it's windows or mac os, in the itch file there should be always:
     * - forward slashes, no backslashes
     * - no drive name on Windows (C:\, etc)
     * - no drive name on Mac OS (/Volumes/VolumeName/, etc)
     * @param name Track name with absolute path
     * @return Track name with all slashes replaced with forward slashes
     */
    private String getUniformTrackName(String name) {
        // make all forward slashes on windows
        name = name.replaceAll("\\\\", "/");

        // remove drive on windows
        name = name.replaceAll("^[a-zA-Z]\\:\\/", "");

        // remove drive on mac os
        name = name.replaceAll("^/Volumes/[^/]+/", "");

        return name;
    }

    public void writeTo(File outFile) throws ItchLibraryException {
        try {
            writeTo(new FileOutputStream(outFile));
        } catch (FileNotFoundException e) {
            throw new ItchLibraryException("Error writing to file " + outFile.getName(), e);
        }
    }

}
